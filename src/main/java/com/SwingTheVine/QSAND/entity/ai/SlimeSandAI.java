package com.SwingTheVine.QSAND.entity.ai;

import java.util.Random;

import com.SwingTheVine.QSAND.entity.SlimeSand;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.world.World;

public class SlimeSandAI extends EntityAIBase {

	private World world;
	private Class <? extends Entity> player;
	private SlimeSand slime;
	private final EntityAINearestAttackableTarget.Sorter sorter;
	
	private Random random = new Random(30);
	
	public SlimeSandAI(World world, Class <? extends Entity> player, SlimeSand slime) {
		this.world = world;
		this.player = player;
		this.slime = slime;
		this.sorter = new EntityAINearestAttackableTarget.Sorter(slime);
	}

	@Override
	public boolean shouldExecute() {
		// TODO Auto-generated method stub
		return false;
	}
	
	
}
