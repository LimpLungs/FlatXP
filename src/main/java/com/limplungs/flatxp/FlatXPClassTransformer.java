package com.limplungs.flatxp;

import static org.objectweb.asm.Opcodes.*;

import java.util.Arrays;

import javax.naming.Context;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Type;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.inventory.ContainerEnchantment;
import net.minecraft.launchwrapper.IClassTransformer;

public class FlatXPClassTransformer implements IClassTransformer 
{
	private static final String[] classes = {"net.minecraft.inventory.ContainerEnchantment", "net.minecraft.entity.player.EntityPlayer"};

	@Override
	public byte[] transform(String name, String transformName, byte[] className) 
	{
		
        boolean isObfuscated = !name.equals(transformName);
        
        
        for (int i = 0; i < classes.length; i++)
        {
        	if (classes[i].indexOf(transformName) >= 0)
        	{
        		transform(i, className, isObfuscated);
        	}
        }
        
        return className;
	}
	
	private static byte[] transform(int index, byte[] className, boolean isObfuscated)
    {
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(className);
            classReader.accept(classNode, 0);

            if (index == 0)
            {
            	System.out.println("Transforming: " + classes[index]);
                transformEnchantItem(classNode, isObfuscated);
            }
            
            if (index == 1)
            {
            	System.out.println("Transforming: " + classes[index]);
                transformXPCap(classNode, isObfuscated);
            }

            ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
            classNode.accept(classWriter);
            
            return classWriter.toByteArray();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        
        return className;
    }

	private static void transformEnchantItem(ClassNode classNode, boolean isObfuscated) 
	{
		//Data found using MCP Mapping Viewer
		final String ENCHANT_ITEM = isObfuscated ? "a" : "enchantItem";
        final String ENCHANT_ITEM_DESC = isObfuscated ? "(Laeb;I)Z" : "(Lnet/minecraft/entity/player/EntityPlayer;I)Z";
        
        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(ENCHANT_ITEM) && method.desc.equals(ENCHANT_ITEM_DESC))
            {
                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                	// Check and find the location of  "playerIn.onEnchant(itemstack, i);", then set the targetNode to that node.
                    if (instruction.getOpcode() == ALOAD && instruction.getNext().getOpcode() == ALOAD)
                    {
                    	System.out.println("true test");
                        if (((VarInsnNode) instruction).var == 1 && ((VarInsnNode) instruction.getNext()).var == 3)
                        {
                        	System.out.println("set to enchant cost node");
                            	
                            /* this */
                            VarInsnNode curr1 = new VarInsnNode(ALOAD, 0);
                            	
                            /* id */
                            VarInsnNode curr2 = new VarInsnNode(ILOAD, 2);
                            	
                            /* .enchantLevels */
                            MethodInsnNode curr3 = new MethodInsnNode(INVOKESTATIC, Type.getInternalName(FlatXPClassTransformer.class), "getEnchantLevel", "(Lafz;I)I", false);
                            
                            instruction = instruction.getNext();

                            method.instructions.insert(instruction, curr3);
                            method.instructions.insert(instruction, curr2);
                            method.instructions.insert(instruction, curr1);
                                
                            method.instructions.remove(curr3.getNext());
                            System.out.println("done??!!!");
                            
                            break;
                        }
                    }
                }
            }
        }
	}
	
	// NOTE TO SELF:
	// Use this, replace encahntItem where it says playerIn.onEnchant(itemstack, i);
	// replace "i" with this, using MethodInsnNode above^^
	//
	public int getEnchantLevel(ContainerEnchantment table, int id)
	{
		System.out.println(table.enchantLevels[id]);
		return table.enchantLevels[id];
	}

	private static void transformXPCap(ClassNode classNode, boolean isObfuscated) 
	{
		final String XP_BAR_CAP = isObfuscated ? "dh" : "xpBarCap";
        final String XP_BAR_CAP_DESC = isObfuscated ? "()I" : "()I";
        
        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(XP_BAR_CAP) && method.desc.equals(XP_BAR_CAP_DESC))
            {
            	AbstractInsnNode targetNode = null;
            	
                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if (instruction.getOpcode() == ALOAD)
                    {
                        if (((VarInsnNode) instruction).var == 0 && instruction.getNext().getOpcode() == DUP)
                        {
                            targetNode = instruction;
                            break;
                        }
                    }
                }
                if (targetNode != null)
                {
                	// Remove math from method xpBarCap in EntityPlayer;
                	for (int i = 0; i < 57; i++)
                    {
                    //    targetNode = targetNode.getNext();
                    //    method.instructions.remove(targetNode.getPrevious());
                    }
                	
                	
                	// Adds back return statement.
                	//Code:
                	//0: bipush 100;
                }
                else
                {
                    System.out.println("Error Transforming FlatXP xpCap!!!");
                }
            }
        }
	}
}
