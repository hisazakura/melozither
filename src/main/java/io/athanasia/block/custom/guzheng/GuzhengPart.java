package io.athanasia.block.custom.guzheng;

import net.minecraft.util.StringIdentifiable;

public enum GuzhengPart implements StringIdentifiable {
	HEAD("head"),
	FOOT("foot");

	private final String name;

	private GuzhengPart(String name) {
		this.name = name;
	}

	public String toString() {
		return this.name;
	}

	@Override
	public String asString() {
		return this.name;
	}

}
