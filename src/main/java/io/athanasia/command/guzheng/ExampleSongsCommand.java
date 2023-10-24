package io.athanasia.command.guzheng;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.command.CommandManager.RegistrationEnvironment;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class ExampleSongsCommand {
	public static void register(CommandDispatcher<ServerCommandSource> dispatcher, CommandRegistryAccess registryAccess,
			RegistrationEnvironment environment) {
		dispatcher.register(
				CommandManager.literal("melozither")
						.then(CommandManager.literal("examples")
								.then(CommandManager.literal("Guzheng:BrokenMoon")
										.executes(ExampleSongsCommand::getBrokenMoon))
								.then(CommandManager.literal("Guzheng:BadApple")
										.executes(ExampleSongsCommand::getBadApple))));
	}

	public static int getBrokenMoon(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null)
			return 1;

		NbtString title = NbtString.of("Broken Moon");
		NbtString author = NbtString.of("U2 Akiyama");
		NbtList pages = new NbtList();
		pages.add(NbtString.of(
				"{\"text\":\"<4>kl;e[r^v]^v[ea][r^v][tb]b[es][b;][nl][nk][de][nr][a;]^n[;n][eb][^vr]^v[ae][r^v][tb]b[ys][ub][ni][n^u]<1>[yud]^u<2>u<4>[ny][nu]n[yd][tn][^vr]^v[ta][^ve][rb]b[se][br][tn]<2>n;[ed]r<4>[en][a;]^n[n;][bl][;^v]<2>^v<1>l;<4>[ae][r^v][tb][br][sl]b[n;]ndn[ka][^nl][n;]\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[be][^kr^v]^v[kea][^k^vr][ltb]b[se][;b][nl][nk][ed][nr][;a]^n[n;][be][^v^kr]^v[eak][r^v^k][lbt]b[ys][ub][ni][n^u]<1>[yud]^u<2>u<4>[ny][un]n[;dy][nlt][r^v^k]^v[atl][^vke][rlb]b[kes][blr][;tn]<2>n;[ed]r<4>[en][;a]^n[ne;][blr][^v;]<2>^v<1>l;<4>[ea][r^v][tb][rb]\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[sty]b[neu]ndn[nk][ln][;d][rn][y^v][u^v][ta][r^v][tby]b[es][rb][^;n][ne]<1>[d^;;]e<2>^;<4>[ln][;ha]^n[kn][lb][^v;][^ve][^kra][k^ve][b^kr]<2>be[rs]t<4>[be][^;rn]<2>n[e;]<4>[^;dr][yn][net]n[dt][yn][^t^vu]^v[ay][u^v][bt]bs[be][nr][nt]<1>[edr]t<2>r<4>[ne][;a]^n\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[;n][lb][;^v]<2>^v<1>l;<4>[ae][r^v][tb][br][ys]b<16>[tuen]<4>kl;e<6>[^vr]<2>^v<4>[ea][^vr]<2>tb<4>b[se][;b][nl][nk][ed][nr][a;]^n[n;][be]<6>[r^v]<2>^v<4>[ea][^vr]<2>tb<4>b[sy][ub][in]<2>^un<1>[uyd]^u<2>u<4>[ny]<2>un<4>n[dy][tb]<6>[^vr]<2>^v<4>[at][e^v]<2>rb\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"<4>b[se][rb]<6>[tn]<2>[;n][de]r<4>[ne][;a]^n[n;][lb]<6>[;^v]<1>[^vl];<4>[ae][r^v]<2>tb<4>[rb][ls]b<6>[n;]<2>n<4>dn[ak][l^n][n;][be]<6>[^vr^k]<2>^v<4>[eak][^kr^v]<2>[tl]b<4>b[se][b;][ln][nk][ed][nr][;a]^n[n;][be]<6>[r^k^v]<2>^v<4>[kae][^v^kr]<2>[tl]b<4>b[sy][ub]\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[ni]<2>^un<1>[ydu]^u<2>u<4>[ny]<2>un<4>n[;yd][tlb]<6>[r^k^v]<2>^v<4>[alt][e^vk]<2>[rl]b<4>b[kes][rlb]<6>[t;n]<2>[n;][de]r<4>[en][;a]^n[e;n][blr]<6>[;^v]<1>[l^v];<4>[ea][r^v]<2>tb<4>[br][tys]b<6>[une]<2>n<4>dn<2>kn<4>[ln][;d][rb][^vy]<2>u^v<4>[ta][r^v]<2>[yt]b<4>b[se]\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[rb][^;n][en]<1>[^;;d]e<2>^;<4>[nl][ah;]^n[nk][bl][^v;]<2>e^v<4>[r^ka][^vke]<2>[^kr]bbe[sr]t<4>[eb]<6>[nr^;]<2>[;ne]<4>[r^;d][ny]<2>[et]n<4>n[dt][by]<6>[^t^vu]<2>^v<4>[ay][u^v]<2>tb<4>bs[be][rn]<2>tn<1>[erd]t<2>r<4>[en][;a]^n[;n][bl]<6>[;^v]<1>[^vl];<4>[ae][r^v]<2>tb<4>[br][ys]b<1>[nuet]\"}"));

		ItemStack songBook = new ItemStack(Items.WRITTEN_BOOK, 1);
		songBook.setSubNbt("title", title);
		songBook.setSubNbt("author", author);
		songBook.setSubNbt("pages", pages);

		player.giveItemStack(songBook);

		return 1;
	}

	public static int getBadApple(CommandContext<ServerCommandSource> context) throws CommandSyntaxException {
		ServerPlayerEntity player = context.getSource().getPlayer();
		if (player == null)
			return 1;

		NbtString title = NbtString.of("Bad Apple");
		NbtString author = NbtString.of("ZUN");
		NbtList pages = new NbtList();
		pages.add(NbtString.of(
				"{\"text\":\"<4>cnbn^vnvncnbn^vnvnc[r;n]b[;rn]^v[r;n]v[r;n]c[;rn]b[r;n]^v[;rn]v[r;n]<2>[;c]j[k;rn]^k[;b]j[kr;n]^k[;^v]j[kr;n]^k[ev]r[;;rn]j[;c]j[kr;n]^k[;b]j[kr;n]^k[;^v]j[tr;n]^t[;v]l[^k;rn]k[gx]k[^kr;^v]g[^kv]h[jr;^v]^k[gc]k[^k;r^v]g[^kx]h[j;r^v]^k[hx]j\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[kr;^v]g[hv]j[kr;^v]g[hx]j[k;rc]j[gn]l[^kr;v]k[;c]j[k;rn]^k[;b]j[kr;n]^k[;^v]j[k;rn]^k[ev]r[;r;n]j[;c]j[kr;n]^k[;b]j[kr;n]^k[;^v]j[t;rn]^t[;v]l[^k;rn]k[gx]k[^kr;^v]g[^kv]h[jr;^v]^k[gc]k[^k;r^v]g[^kx]h[jr;^v]^k[hx]j[kr;^v]g[hv]j[kr;^v]g[hx]j[k;rc]\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"j[gn]l[^kr;v]k[;jgc]j[kkhn]^k[;^kjc]j[klkn]^k[;;^kc]j[kn]^k[ejgc]r[;;en]j[;^k;c]j[kn]^k[;jgc]j[kn]^k[;;^kc]j[tlkn]^t[;j^kc]l[^khkn]k[ggjx]k[^khk^v]g[^kj^kx]h[jlk^v]^k[g^k;x]k[^k^v]g[^kklx]h[j^kj^v]^k[hkkhx]j[kg^db]g[hhkx]j[kj^kb]g[hkhx]j[kgjb]j\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[gghx]l[^khkb]k[;jgc]j[khkn]^k[;^kjc]j[kkln]^k[;;^kc]j[kn]^k[eklc]r[;len]j[;;rc]j[kn]^k[;;rc]j[kn]^k[;tec]j[tn]^t[;^trc]l[^kn]k[g;rx]k[^kte^v]g[^kr^tx]h[jty^v]^k[g^tux]k[^k^v]g[^kytx]h[jr^t^v]^k[hytx]j[kb]g[htex]j[kb]g[h^trx]j[kb]j[gytx]l[^kb]k\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"<8>[sjgxn]^d<4>[jg][kh]<8>[s^kj][dkhax]<4>f<12>[g^d]<8>[dkh][^dkhcs]<4>[gj^k][jg]<8>^d[ghf][^dhfsc]<4>[jgc][^dg]<8>c<4>[gjc]c[sgjnx]c[^dx]c[jgx][hkc][s^kjx]c[dhkax]^v[fx][j^k^v]x^v[dlkx]^v[^dgjcs]b[gc][;kb][^d;kc]b[gc]b[^dcs]bcb[jlc][^k;b][elx]^v\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"<2>[gr;xn]g[jc]g[gx]j[^kc];[er;x];[^ktec]j[g^trx]g[jc]g[hetax]h[k^v]h[hx]k[l^k;^v]e[rx]e[l^v]k[htex]h[k^v]h[jetsc]j[^kb]j[j^trc]^k[;;rb]r[tc]r[;b]^k[jelc]j[^kb]j[jelsc]j[^kb]j[j;rc]^k[;^k;b]r[tc]r[;b]^k[jtex]j[^k^v]j[g;rxn]g[jc]g[gx]j[^kc];[e;rx];\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[^ktec]j[gr^tx]g[jtyc]g[htexa]h[k^v]h[hx]k[lr^t^v]e[rx]e[l^v]k[hytx]h[k^v]h[jlecs]j[^kb]j[jc]^k[;;kb]r[tc]r[;b]^k[j^tuc]j[^kb]j[jtysc]j[^kb]j[j^trc]^k[;b]r[ttec]r[;b]^k[j^trx]j[^k^v]j<4>[s^k^khnc][ghghsn][cn][^k^khns][nc][^dgns][hgcn][^gksn][d^kh^nv]\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[ghd^n][g^d^nv][h^kd^n][v^n][^kh^nd][lkv^n][;^k^nd][^d^kh^va][gh^da][^va][^kha^d][^va][hg^da][h^k^va][l^;a^d][^d^k;a^v][a^d][^va][^da][lk^va][^kh^da][ghv^n][g^dd^n][sh^knc][hgsn][cn][^khns][nc][^dgsn][ghcn][^gksn][dh^k^nv][ghd^n][^dg^nv][^khd^n]\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[^nv][h^kd^n][lkv^n][^k;^nd][^d^kh^va][gha^d][a^v][^kh^da][a^v][hg^da][^kh^va][l^;a^d][^d;^ka^v][^da][a^v][a^d][kl^va][^da][^k;v^n][^nd]<2>[^kje^t^tcn]^g[h;e;ens]r[jnc]^k[^k;^t^tsn]r[tnc]^;[g;^k;sn]^;[h^t;ev^n]t[^tkt^;^nd]y[u^ke^ta^v]y[^the;a^d]e[g^u^k;a^v]u\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[y^ke^t^da]^t[iia^v]u[^k^t^tea^d]e[l;yt^nv]l[;;^tud^n]h[^kje^tcn]^g[h;;esn]r[jnc]^k[^k;^tens]r[tnc]^;[;he;ns]^;[^k^te^tv^n]t[^;^ty^u^nd]y[u;u^t^va]y[^ta^d]e[^u^va]u[y^da]^t[liity^va]u[^k^t^tea^d]e[h;;ev^n]l[g;^k;d^n]h[^kje^tnc]^g[;h;ens]r[jnc]^k[^k;e^tsn]r[tnc]^;[;g^k;ns]^;\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[^tk^;t^nd]y[^ku^tea^v]y[h^te;^da]e[^ug^k;^va]u[^ky^tea^d]^t[iia^v]u[^k^t^te^da]e[l;ty^nv]l[;;u^td^n]h[^kj^tenc]^g[h;;ens]r[jnc][h^te;v^n]t^k[;^ke^tsn]r[tcn]^;[h;;esn]^;[^t^ke^t^nv]t[^tlyt^nd]y\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[;u^tua^v]y[^t^da]e[^ua^v]u[y^da]^t[iia^v]u[^ta^d]e[;v^n]l[;^nd]h<4>[^khc][hgsn]c[^khsn]c[^dgns][ghc][khsn][h^kv][hgd^n][g^dv][h^kd^n]v[h^kd^n][klv][^k;^nd][^kh^v][hga^d]^v[^kh^da]^v[gha^d][h^k^v]\"}"));
		pages.add(NbtString.of(
				"{\"text\":\"[^;^k^da][k;^v][a^d]^v[a^d][jl^v][h^k^da][gh^v][^dga^d][^khc][hgsn]c[h^kns]c[g^dsn][hgc][^gksn][h^kv][hgd^n][dgv][^khd^n]v[^kh^nd][lkv][^k;d^n][^kh^v][gh^da]^v[h^ka^d]^v[gha^d][h^k^v][^;^ka^d][^k;^v][^da]^v[^da][kl^v][^da][^k;^v]<1>[^da]\"}"));

		ItemStack songBook = new ItemStack(Items.WRITTEN_BOOK, 1);
		songBook.setSubNbt("title", title);
		songBook.setSubNbt("author", author);
		songBook.setSubNbt("pages", pages);

		player.giveItemStack(songBook);

		return 1;
	}
}
