package io.athanasia.block.custom.guzheng;

import org.jetbrains.annotations.Nullable;

import io.athanasia.MeloZither;
import io.athanasia.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

public class GuzhengBlockEntity extends BlockEntity {
	// default script
	public String SCRIPT = "<8>[la]kj [ls]kj [la]k[js]g[hd]j[kd] [la]kj [ls]kj [la]k[js] [kd]   [fb]g[ha]j[ld]kl [ls]kl [la];[;s]e[;d]lk [la]kj [ls]kj [la]k[js]g[hd]j[ha]";
	public GuzhengSongData SONG_DATA = GuzhengParser.parse(this.SCRIPT);
	public int TICK_COUNT = -1;
	public boolean isPlaying = false;

	public GuzhengBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GUZHENG_BLOCK_ENTITY, pos, state);
	}

	private GuzhengPart getPart() {
		World world = this.getWorld();
		if (world == null)
			throw new NullPointerException("Something went wrong");
		// return GuzhengPart.HEAD; // not a good idea honestly

		BlockPos blockPos = this.getPos();
		BlockState blockState = world.getBlockState(blockPos);
		GuzhengPart part = blockState.get(GuzhengBlock.PART);

		return part;
	}

	@Nullable
	public String setScript(String script) {
		return setScript(script, null, null);
	}

	public String setScript(String script, @Nullable String title, @Nullable String author) {
		// only set the head
		if (this.getPart() != GuzhengPart.HEAD)
			return this.getBlockEntityOfOtherPart().setAndParseScript(script, title, author);
		return setAndParseScript(script, title, author);
	}

	@Nullable
	private String setAndParseScript(String script, @Nullable String title, @Nullable String author) {
		try {
			this.SONG_DATA = GuzhengParser.parse(script);
			this.SCRIPT = script;
		} catch (IllegalArgumentException e) {
			return e.getMessage();
		}

		// add title and author if written book
		if (title != null && author != null) {
			this.SONG_DATA.setTitle(title);
			this.SONG_DATA.setAuthor(author);
		}

		markDirty();

		return null;
	}

	private GuzhengBlockEntity getBlockEntityOfOtherPart() {
		World world = this.getWorld();
		if (world == null)
			throw new NullPointerException("Something went wrong!");
		BlockPos blockPos = this.getPos();
		BlockState blockState = world.getBlockState(blockPos);
		GuzhengPart part = blockState.get(GuzhengBlock.PART);
		Direction direction = blockState.get(Properties.HORIZONTAL_FACING);

		if (part == GuzhengPart.FOOT)
			return (GuzhengBlockEntity) world.getBlockEntity(blockPos.offset(direction));
		return (GuzhengBlockEntity) world.getBlockEntity(blockPos.offset(direction.getOpposite()));
	}

	public void play() {
		// only play on head
		if (this.getPart() != GuzhengPart.HEAD) {
			this.getBlockEntityOfOtherPart().play();
			return;
		}

		isPlaying = true;

		// notify players
		if (this.SONG_DATA.getTitle() == null || this.SONG_DATA.getAuthor() == null)
			return;

		for (PlayerEntity player : this.getWorld().getPlayers()) {
			if (player.squaredDistanceTo(this.getPos().toCenterPos()) <= 16 * 16) {
				player.sendMessage(Text.translatable("zither.nowPlaying",
						this.SONG_DATA.getTitle(),
						this.SONG_DATA.getAuthor())
						.setStyle(Style.EMPTY.withFormatting(Formatting.GREEN)), true);
			}
		}
	}

	public void stop() {
		this.isPlaying = false;
		this.TICK_COUNT = -1;
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	public static Vec3d getCenter(Vec3i vec) {
		return new Vec3d(
				(double) vec.getX() + 0.5,
				(double) vec.getY() + 0.5,
				(double) vec.getZ() + 0.5);
	}

	public static Vec3d getCenter(Vec3i vec1, Vec3i vec2) {
		return new Vec3d(
				(double) (vec1.getX() + vec2.getX()) / 2.0,
				(double) (vec1.getY() + vec2.getY()) / 2.0,
				(double) (vec1.getZ() + vec2.getZ()) / 2.0);
	}

	private static Vec3d randomlyOffsetPositionBetween(BlockPos blockPos1, BlockPos blockPos2) {
		if (Math.random() < 0.5)
			return offsetPositionRandomly(blockPos1);
		return offsetPositionRandomly(blockPos2);
	}

	private static Vec3d offsetPositionRandomly(BlockPos pos) {
		return new Vec3d(
				(double) pos.getX() + Math.random(),
				(double) pos.getY() + Math.random(),
				(double) pos.getZ() + Math.random());
	}

	private static void playNote(World world, Vec3d soundPos, Vec3d particlePos, GuzhengNote note) {
		world.playSound(null, soundPos.getX(), soundPos.getY(), soundPos.getZ(), note.getSoundEvent(),
				SoundCategory.BLOCKS, 1f, note.getPitch());
		if (world instanceof ServerWorld serverWorld) {
			serverWorld.spawnParticles(ParticleTypes.NOTE, (double) particlePos.getX(), (double) particlePos.getY(),
					(double) particlePos.getZ(), 1, 0, 0, 0, 1 / 24.0);
			// (double) particlePos.getX(),
			// (double) particlePos.getY(),
			// (double) particlePos.getZ(),
			// (double) 1 / 24.0, 0.0, 0.0);
		}

	}

	public static <E extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, E entity) {
		if (!(entity instanceof GuzhengBlockEntity))
			return;
		GuzhengBlockEntity guzhengBlockEntity = (GuzhengBlockEntity) entity;
		GuzhengBlockEntity otherBlockEntity = guzhengBlockEntity.getBlockEntityOfOtherPart();

		BlockPos otherBlockPos = (otherBlockEntity != null) ? otherBlockEntity.getPos() : blockPos;
		Vec3d soundPos = getCenter(blockPos, otherBlockPos);

		if (!guzhengBlockEntity.isPlaying)
			return;

		if (guzhengBlockEntity.getPart() != GuzhengPart.HEAD)
			return;

		for (GuzhengNote note : guzhengBlockEntity.SONG_DATA.getNotesAtTime(guzhengBlockEntity.TICK_COUNT)) {
			Vec3d particlePos = ((otherBlockPos != null) ? randomlyOffsetPositionBetween(blockPos, otherBlockPos)
					: offsetPositionRandomly(blockPos)).withAxis(Axis.Y, (double) blockPos.getY());
			playNote(world, soundPos, particlePos, note);
		}

		guzhengBlockEntity.TICK_COUNT++;

		// Stop the script after the song is completed
		if (guzhengBlockEntity.TICK_COUNT > guzhengBlockEntity.SONG_DATA.getLength())
			guzhengBlockEntity.stop();
	}

	@Override
	public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		super.readNbt(nbt, registryLookup);

		String part = nbt.getString("part");
		if (part == "foot")
			return;

		String title = nbt.getString("title");
		String author = nbt.getString("author");
		String script = nbt.getString("script");

		String result;
		if (title.equals("") || author.equals(""))
			result = this.setAndParseScript(script, null, null);
		else
			result = this.setAndParseScript(script, title, author);

		if (result != null)
			MeloZither.LOGGER.info(result);
	}

	@Override
	protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
		nbt.putString("part", (getPart() == GuzhengPart.HEAD) ? "head" : "foot");
		if (getPart() == GuzhengPart.HEAD) {
			if (this.SONG_DATA.getAuthor() != null && this.SONG_DATA.getTitle() != null) {
				nbt.putString("title", this.SONG_DATA.getTitle());
				nbt.putString("author", this.SONG_DATA.getAuthor());
			}
			nbt.putString("script", this.SCRIPT);
		}
		super.writeNbt(nbt, registryLookup);
	}

}
