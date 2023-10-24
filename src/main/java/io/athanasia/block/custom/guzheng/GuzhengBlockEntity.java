package io.athanasia.block.custom.guzheng;

import org.jetbrains.annotations.Nullable;

import io.athanasia.block.ModBlockEntities;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class GuzhengBlockEntity extends BlockEntity {
	// default script
	public String SCRIPT = "<8>[la]kj [ls]kj [la]k[js]g[hd]j[kd] [la]kj [ls]kj [la]k[js] [kd]   [fb]g[ha]j[ld]kl [ls]kl [la];[;s]e[;d]lk [la]kj [ls]kj [la]k[js]g[hd]j[ha]";
	public GuzhengSongData parsedScript = GuzhengParser.parse(this.SCRIPT);
	public int TICK_COUNT = -1;
	public boolean isPlaying = false;

	public GuzhengBlockEntity(BlockPos pos, BlockState state) {
		super(ModBlockEntities.GUZHENG_BLOCK_ENTITY, pos, state);
	}

	@Nullable
	public String setScript(String script) {
		return setScript(script, null, null);
	}

	@Nullable
	public String setScript(String script, @Nullable String title, @Nullable String author) {
		String previousScript = this.SCRIPT;
		this.SCRIPT = script;
		try {
			this.parsedScript = GuzhengParser.parse(this.SCRIPT);
		} catch (IllegalArgumentException e) {
			this.SCRIPT = previousScript;
			return e.getMessage();
		}
		try {
			GuzhengBlockEntity otherBlockEntity = getBlockEntityOfOtherPart();
			if (otherBlockEntity.SCRIPT != this.SCRIPT)
				otherBlockEntity.setScript(script);
		} catch (NullPointerException e) {
			this.SCRIPT = previousScript;
			return e.getMessage();
		}
		if (title != null && author != null) {
			this.parsedScript.setTitle(title);
			this.parsedScript.setAuthor(author);
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

	public void playScript() {
		GuzhengBlockEntity otherBlockEntity = getBlockEntityOfOtherPart();
		if (otherBlockEntity == null)
			return;
		if (otherBlockEntity.isPlaying)
			return;
		isPlaying = true;
		if (this.parsedScript.getTitle() == null || this.parsedScript.getAuthor() == null)
			return;
		for (PlayerEntity player : this.getWorld().getPlayers()) {
			if (player.squaredDistanceTo(this.getPos().toCenterPos()) <= 16 * 16) {
				player.sendMessage(Text.translatable("zither.nowPlaying", this.parsedScript.getTitle(),
						this.parsedScript.getAuthor()).setStyle(Style.EMPTY.withFormatting(Formatting.GREEN)), true);
			}
		}
	}

	public void stopScript() {
		this.isPlaying = false;
		this.TICK_COUNT = -1;
	}

	public BlockEntityUpdateS2CPacket toUpdatePacket() {
		return BlockEntityUpdateS2CPacket.create(this);
	}

	@Override
	public NbtCompound toInitialChunkDataNbt() {
		return createNbt();
	}

	private static Vec3d randomizeLocation(BlockPos blockPos1, BlockPos blockPos2) {
		if (Math.random() < 0.5)
			return new Vec3d(
					blockPos1.getX() + Math.random(),
					blockPos1.getY() + Math.random(),
					blockPos1.getZ() + Math.random());
		return new Vec3d(
				blockPos2.getX() + Math.random(),
				blockPos2.getY() + Math.random(),
				blockPos2.getZ() + Math.random());
	}

	private static void playNote(GuzhengBlockEntity entity, World world, BlockPos blockPos, GuzhengNote note) {
		world.playSound(null, blockPos, note.getSoundEvent(), SoundCategory.BLOCKS, 1f, note.getPitch());
		BlockPos otherBlockPos = entity.getBlockEntityOfOtherPart().getPos();
		Vec3d randomizedLocation = randomizeLocation(blockPos, otherBlockPos);
		world.addParticle(ParticleTypes.NOTE,
				(double) randomizedLocation.getX(),
				(double) blockPos.getY() + 0.4,
				(double) randomizedLocation.getZ(),
				(double) 1 / 24.0, 0.0, 0.0);
	}

	public static <E extends BlockEntity> void tick(World world, BlockPos blockPos, BlockState blockState, E entity) {
		if (!(entity instanceof GuzhengBlockEntity))
			return;
		GuzhengBlockEntity guzhengBlockEntity = (GuzhengBlockEntity) entity;

		if (!guzhengBlockEntity.isPlaying)
			return;

		for (GuzhengNote note : guzhengBlockEntity.parsedScript.getNotesAtTime(guzhengBlockEntity.TICK_COUNT)) {
			playNote(guzhengBlockEntity, world, blockPos, note);
			// world.playSound(null, blockPos, note.getSoundEvent(), SoundCategory.BLOCKS,
			// 1f, note.getPitch());
			// BlockPos otherBlockPos =
			// guzhengBlockEntity.getBlockEntityOfOtherPart().getPos();
			// Vec3d randomizedLocation = randomizeLocation(blockPos, otherBlockPos);
			// world.addParticle(ParticleTypes.NOTE,
			// (double) randomizedLocation.getX(),
			// (double) blockPos.getY() + 0.4,
			// (double) randomizedLocation.getZ(),
			// (double) 1 / 24.0, 0.0, 0.0);
		}

		if (guzhengBlockEntity.TICK_COUNT > guzhengBlockEntity.parsedScript.getLength()) {
			// guzhengBlockEntity.isPlaying = false;
			// guzhengBlockEntity.TICK_COUNT = -1;
			guzhengBlockEntity.stopScript();
		}

		guzhengBlockEntity.TICK_COUNT++;
	}

	@Override
	public void readNbt(NbtCompound nbt) {
		super.readNbt(nbt);
		parsedScript = GuzhengSongData.fromJson(nbt.getString("script"));
		SCRIPT = parsedScript.geScript();
	}

	@Override
	protected void writeNbt(NbtCompound nbt) {
		nbt.putString("script", parsedScript.toJson());
		super.writeNbt(nbt);
	}

}
