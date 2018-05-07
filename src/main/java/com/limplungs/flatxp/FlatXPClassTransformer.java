package com.limplungs.flatxp;

import static org.objectweb.asm.Opcodes.*;

import java.util.Arrays;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.launchwrapper.IClassTransformer;

public class FlatXPClassTransformer implements IClassTransformer 
{
	private static final String[] classes = {"net.minecraft.entity.player.EntityPlayer"};

	@Override
	public byte[] transform(String name, String transformName, byte[] className) 
	{
		
        boolean isObfuscated = !name.equals(transformName);
        
        int index = Arrays.asList(classes).indexOf(transformName);
        return index > -1 ? transform(index, className, isObfuscated) : className;
	}
	
	private static byte[] transform(int index, byte[] className, boolean isObfuscated)
    {
        System.out.println("Transforming: " + classes[index]);
        try
        {
            ClassNode classNode = new ClassNode();
            ClassReader classReader = new ClassReader(className);
            classReader.accept(classNode, 0);

            switch(index)
            {
                case 0:
                    transformXPCap(classNode, isObfuscated);
                    transformOnEnchant(classNode, isObfuscated);
                    break;
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

	private static void transformOnEnchant(ClassNode classNode, boolean isObfuscated) 
	{
		final String ON_ENCHANT = isObfuscated ? "a" : "onEnchant";
        final String ON_ENCHANT_DESC = isObfuscated ? "(Lain;I)V" : "(Lnet/minecraft/item/ItemStack;I)V";
        
        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(ON_ENCHANT) && method.desc.equals(ON_ENCHANT_DESC))
            {
            	AbstractInsnNode targetNode = null;
            	
                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                    if (instruction.getOpcode() == ALOAD)
                    {
                        System.out.println(instruction.getNext().getOpcode() == DUP);
                        if (((VarInsnNode) instruction).var == 0 && instruction.getNext().getOpcode() == DUP)
                        {
                            targetNode = instruction;
                            break;
                        }
                    }
                }
                if (targetNode != null)
                {
                	// Remove math from method onEnchant in EntityPlayer;
                	for (int i = 0; i < 8; i++)
                    {
                        targetNode = targetNode.getNext();
                        method.instructions.remove(targetNode.getPrevious());
                    }
                }
                else
                {
                    System.out.println("Error Transforming FlatXP onEnchant!!!");
                }
            }
        }
	}

	private static void transformXPCap(ClassNode classNode, boolean isObfuscated) 
	{
		
	}
}
