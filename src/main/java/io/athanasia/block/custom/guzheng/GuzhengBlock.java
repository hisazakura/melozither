package io.athanasia.block.custom.guzheng;

import java.util.List;
import java.util.StringJoiner;
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
		BlockEntity blockEntity = world.getBlockEntity(pos);
		if (!(blockEntity instanceof GuzhengBlockEntity))
			return ActionResult.SUCCESS;

		GuzhengBlockEntity guzhengBlockEntity = (GuzhengBlockEntity) blockEntity;

		ItemStack itemInHand = player.getStackInHand(hand);
		if (!itemInHand.getItem().equals(Items.AIR) && (itemInHand.getItem().equals(Items.WRITABLE_BOOK)
				|| itemInHand.getItem().equals(Items.WRITTEN_BOOK))) {

			List<String> pages = itemInHand.getNbt().getList("pages", NbtElement.STRING_TYPE).stream()
					.map(NbtElement::asString)
					.collect(Collectors.toList());

			StringJoiner stringJoiner = new StringJoiner("");
			pages.forEach(stringJoiner::add);
			String script = stringJoiner.toString();

			String err = guzhengBlockEntity.setScript(script);
			if (err != null) player.sendMessage(Text.literal(err));
		}

		guzhengBlockEntity.playScript();
		return ActionResult.SUCCESS;
	}

	@Override
	public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		world.playSound(null, pos, SoundEvents.BLOCK_NOTE_BLOCK_BANJO.value(), SoundCategory.BLOCKS, 1f, 1.414214f);
	}

	@SuppressWarnings("deprecation")
	@Override
	public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState,
			WorldAccess world, BlockPos pos, BlockPos neighborPos) {
		if (direction == GuzhengBlock.getDirectionTowardsOtherPart(state.get(PART), state.get(FACING))) {
			if (neighborState.isOf(this) && neighborState.get(PART) != state.get(PART)) {
				return (BlockState) state;
			}
			return Blocks.AIR.getDefaultState();
		}
		return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
	}

	private static Direction getDirectionTowardsOtherPart(GuzhengPart part, Direction direction) {
		return part == GuzhengPart.FOOT ? direction : direction.getOpposite();
	}

	@Override
	public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
		BlockPos blockPos;
		BlockState blockState;
		GuzhengPart guzhengPart;
		if (!world.isClient
				&& player.isCreative()
				&& (guzhengPart = state.get(PART)) == GuzhengPart.FOOT
				&& (blockState = world.getBlockState(
						blockPos = pos
								.offset(GuzhengBlock.getDirectionTowardsOtherPart(guzhengPart, state.get(FACING)))))
						.isOf(this)
				&& blockState.get(PART) == GuzhengPart.HEAD) {
			world.setBlockState(blockPos, Blocks.AIR.getDefaultState(), Block.NOTIFY_ALL | Block.SKIP_DROPS);
			world.syncWorldEvent(player, WorldEvents.BLOCK_BROKEN, blockPos, Block.getRawIdFromState(blockState));
		}
		super.onBreak(world, pos, state, player);
	}

	@Nullable
	@Override
	public BlockState getPlacementState(ItemPlacementContext ctx) {
		Direction direction = ctx.getHorizontalPlayerFacing().rotateCounterclockwise(Axis.Y);
		BlockPos blockPos = ctx.getBlockPos();
		BlockPos blockPos2 = blockPos.offset(direction);
		World world = ctx.getWorld();
		if (world.getBlockState(blockPos2).canReplace(ctx) && world.getWorldBorder().contains(blockPos2)) {
			return (BlockState) this.getDefaultState().with(FACING, direction);
		}
		return null;
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
