package com.limplungs.flatxp;

import static org.objectweb.asm.Opcodes.*;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

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

            switch(index)
            {
            	case 0:
                	System.out.println("Transforming: " + classes[index]);
                    transformEnchantItem(classNode, isObfuscated);
                    break;
            	case 1:
                	System.out.println("Transforming: " + classes[index]);
                    transformXPCap(classNode, isObfuscated);
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

	/* replace line #279 enchantItem in nodes: playerIn.onEnchant(itemstack, i);
	 * 
	 * replace "i" with getEnchantLevel(this, id)
	 * 
	 * "this" is stored in aload_0
	 * "id" is stored under iload when var == 2
	 * 
	 * Original: iload 5
	 *               "i"
	 * 
	 * Target:   aload_0
	 *              "this"
	 *           iload 2 
	 *              "id"
	 *           invokestatic
	 *              "public int getEnchantLevel(ContainerEnchantment table, int id)
	 * 
	 *  ContainerEnchantment == "afz" in MCP Mapping Viewer
	 *	MethodInsnNode is MethodInsnNode(INVOKESTATIC, 
	 *									 Type.getInternalName(FlatXPClassTransformer.class), 
	 *                                   "getEnchantLevel", 
	 *                                   "(Lafz;I)I",
	 *                                   false); 
	 *				
	 */
	private static void transformEnchantItem(ClassNode classNode, boolean isObfuscated) throws NoSuchMethodException, SecurityException 
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
                    if (instruction.getOpcode() == INVOKEVIRTUAL)
                    {
                    	System.out.println("true test");
                        if (((MethodInsnNode) instruction).name.equals("onEnchant") && ((MethodInsnNode) instruction).owner.equals("net/minecraft/entity/player/EntityPlayer"))
                        {
                        	System.out.println("set to method onEnchant node");

                        	
                            /* this */
                            VarInsnNode curr1 = new VarInsnNode(ALOAD, 0);
                            	
                            /* id */
                        	FieldInsnNode curr2 = new FieldInsnNode(GETFIELD, isObfuscated? "afz" : "Lnet/minecraft/inventory/ContainerEnchantment;", isObfuscated ? "g" : "enchantLevels", "[I");

                            /* .enchantLevels */
                        	VarInsnNode curr3 = new VarInsnNode(ILOAD, 2);
                        	
                        	VarInsnNode curr4 = new VarInsnNode(IALOAD, 0);

                            method.instructions.insertBefore(instruction, curr1);
                            method.instructions.insertBefore(instruction, curr2);
                            method.instructions.insertBefore(instruction, curr3);
                            method.instructions.insertBefore(instruction, curr4);
                                
                            method.instructions.remove(curr1.getPrevious());
                            System.out.println("done??!!!");
                            
                            break;
                        }
                    }
                }
            }
        }
	}

	
	// TODO: Replace the EntityPlayer xpBarCap math with a simple return # where # is a config option.
	private static void transformXPCap(ClassNode classNode, boolean isObfuscated) 
	{
		final String XP_BAR_CAP = isObfuscated ? "dh" : "xpBarCap";
        final String XP_BAR_CAP_DESC = isObfuscated ? "()I" : "()I";
        
        for (MethodNode method : classNode.methods)
        {
            if (method.name.equals(XP_BAR_CAP) && method.desc.equals(XP_BAR_CAP_DESC))
            {
                for (AbstractInsnNode instruction : method.instructions.toArray())
                {
                	break;
                	// remove all instructions, add back return #.
                	
                    //method.instructions.remove(instruction);
                }
            }
        }
	}
}
