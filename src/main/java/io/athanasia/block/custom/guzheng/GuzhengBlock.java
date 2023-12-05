package io.athanasia.block.custom.guzheng;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jetbrains.annotations.Nullable;
import io.athanasia.block.ModBlockEntities;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.pathing.NavigationType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Direction.Axis;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class GuzhengBlock extends HorizontalFacingBlock implements BlockEntityProvider {
	public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
	public static final EnumProperty<GuzhengPart> PART = EnumProperty.of("part", GuzhengPart.class);
	protected static final VoxelShape NORTH_SHAPE = Block.createCuboidShape(4.0, 0.0, 4.0, 12.0, 3.0, 16.0);
	protected static final VoxelShape SOUTH_SHAPE = NORTH_SHAPE;
	protected static final VoxelShape WEST_SHAPE = Block.createCuboidShape(0.0, 0.0, 4.0, 16.0, 3.0, 12.0);
	protected static final VoxelShape EAST_SHAPE = WEST_SHAPE;

	public GuzhengBlock(Settings settings) {
		super(settings);
		this.setDefaultState(
				(BlockState) (((BlockState) this.stateManager.getDefaultState()).with(PART, GuzhengPart.FOOT)));
	}

	@Override
	public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand,
			BlockHitResult hit) {
		// If Guzheng is not head part then run action on the other part instead.
		BlockEntity blockEntity = (state.get(PART) == GuzhengPart.HEAD) ? world.getBlockEntity(pos)
				: world.getBlockEntity(
						pos.offset(GuzhengBlock.getDirectionTowardsOtherPart(state.get(PART), state.get(FACING))));

		if (!(blockEntity instanceof GuzhengBlockEntity))
			return ActionResult.SUCCESS;

		GuzhengBlockEntity guzhengBlockEntity = (GuzhengBlockEntity) blockEntity;

		// If the Guzheng is currently playing, stop the playback.
		if (guzhengBlockEntity.isPlaying) {
			guzhengBlockEntity.stop();
			return ActionResult.SUCCESS;
		}

		// Retrieve the item in the player's hand.
		ItemStack itemInHand = player.getStackInHand(hand);

		// Extract script and book metadata from the held book item.
		Map<String, String> bookData = getBookData(itemInHand);

		// Attempt to set the script and metadata on the Guzheng block.
		String err = guzhengBlockEntity.setScript(
				bookData.get("script"),
				bookData.get("title"),
				bookData.get("author"));

		// If there is an error, inform the player and return.
		if (err != null) {
			player.sendMessage(Text.literal(err));
			return ActionResult.SUCCESS;
		}

		// Start playing the Guzheng script.
		guzhengBlockEntity.play();
		return ActionResult.SUCCESS;
	}

	/**
	 * Retrieves information from a writable/written book ItemStack.
	 *
	 * @param itemInHand The ItemStack representing the writable/written book.
	 * @return A map containing book-related information, including the script,
	 *         title, and author.
	 *         The keys are "script," "title," and "author." Returns null if the
	 *         provided ItemStack is not a valid writable/written book.
	 */
	@Nullable
	private Map<String, @Nullable String> getBookData(ItemStack itemInHand) {
		if (itemInHand.getItem().equals(Items.AIR))
			return null;
		if (!itemInHand.getItem().equals(Items.WRITABLE_BOOK) && !itemInHand.getItem().equals(Items.WRITTEN_BOOK))
			return null;

		List<String> pages;
		if (itemInHand.getItem().equals(Items.WRITABLE_BOOK))
			pages = itemInHand.getNbt().getList("pages", NbtElement.STRING_TYPE).stream()
					.map(NbtElement::asString)
					.collect(Collectors.toList());
		else
			pages = itemInHand.getNbt().getList("pages", NbtElement.STRING_TYPE).stream()
					.map(NbtElement::asString)
					.map(page -> page.replace("{\"text\":\"", "").replace("\"}", ""))
					.collect(Collectors.toList());

		String script = String.join("", pages);

		String title = null, author = null;
		if (itemInHand.getItem().equals(Items.WRITTEN_BOOK)) {
			title = itemInHand.getNbt().getString("title");
			author = itemInHand.getNbt().getString("author");
		}

		Map<String, @Nullable String> bookData = new HashMap<>();
		bookData.put("script", script);
		bookData.put("title", title);
		bookData.put("author", author);

		return bookData;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), SoundCategory.BLOCKS, 1f, 1.414214f);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
			WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		// Only handle the other part
		if (direction != GuzhengBlock.getDirectionTowardsOtherPart(state.get(PART), state.get(FACING)))
			return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);

		// Handle unexpected scenarios: remove the block if it's not the expected other part.
		if (!neighborState.isOf(this) || neighborState.get(PART) == state.get(PART))
			return Blocks.AIR.getDefaultState();

		return (BlockState) state;
	}

	private static Direction getDirectionTowardsOtherPart(GuzhengPart part, Direction direction) {
		return part == GuzhengPart.FOOT ? direction : direction.getOpposite();
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		GuzhengPart guzhengPart = state.get(PART);
		BlockPos otherPartPos = pos.offset(GuzhengBlock.getDirectionTowardsOtherPart(guzhengPart, state.get(FACING)));
		BlockState otherPartState = world.getBlockState(otherPartPos);

		// Prevent creative mode drop
		if (!world.isClient
				&& player.isCreative()
				&& guzhengPart == GuzhengPart.FOOT
				&& otherPartState.isOf(this)
				&& otherPartState.get(PART) == GuzhengPart.HEAD) {
			world.setBlockState(otherPartPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
			world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, otherPartPos,
					Block.getRawIdFromState(otherPartState));
		}

		super.onBreak(world, pos, state, player);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		// Sets the guzheng direction on placed
		Direction direction = ctx.getHorizontalPlayerFacing().rotateCounterclockwise(Axis.Y);
		BlockPos blockPos = ctx.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(direction);
		World world = ctx.getWorld();

		if (!world.getBlockState(blockPos2).canReplace(ctx) || !world.getWorldBorder().contains(blockPos2))
			return null;

		return (BlockState) this.getDefaultState().with(FACING, direction);
	}

	@Override
	public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
		Direction direction = GuzhengBlock.getOppositePartDirection(state).getOpposite();
		switch (direction) {
			case NORTH: {
				return NORTH_SHAPE;
			}
			case SOUTH: {
				return SOUTH_SHAPE;
			}
			case WEST: {
				return WEST_SHAPE;
			}
			default:
				return EAST_SHAPE;
		}
	}

	public static Direction getOppositePartDirection(BlockState state) {
		Direction direction = state.get(FACING);
		return state.get(PART) == GuzhengPart.HEAD ? direction.getOpposite() : direction;
	}

	protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
		builder.add(FACING, PART);
	}

	@Override
	public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer,
			ItemStack itemStack) {
		super.onPlaced(world, pos, state, placer, itemStack);
		if (!world.isClient) {
			BlockPos blockPos = pos.offset(state.get(FACING));
			world.setBlockState(blockPos, (BlockState) state.with(PART, GuzhengPart.HEAD), Block.NOTIFY_ALL);
			world.updateNeighbors(pos, Blocks.AIR);
			state.updateNeighbors(world, pos, Block.NOTIFY_ALL);
		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public long getRenderingSeed(BlockState state, BlockPos pos) {
		BlockPos blockPos = pos.offset(state.get(FACING), state.get(PART) == GuzhengPart.HEAD ? 0 : 1);
		return MathHelper.hashCode(blockPos.getX(), pos.getY(), blockPos.getZ());
	}

	@Override
	public boolean canPathfindThrough(BlockState state, BlockView world, BlockPos pos, NavigationType type) {
		return false;
	}

	/* BLOCK ENTITY */

	@Override
	public BlockRenderType getRenderType(BlockState state) {
		return BlockRenderType.MODEL;
	}

	@Override
	public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
		return new GuzhengBlockEntity(pos, state);
	}

	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state,
			BlockEntityType<T> type) {
		if (type != ModBlockEntities.GUZHENG_BLOCK_ENTITY)
			return null;
		return GuzhengBlockEntity::tick;
	}
}
