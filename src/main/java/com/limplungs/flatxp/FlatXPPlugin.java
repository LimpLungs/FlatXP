package com.limplungs.flatxp;

import java.util.Map;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.TransformerExclusions({"com.limplungs.flatxp"})
public class FlatXPPlugin implements IFMLLoadingPlugin 
{

	@Override
	public String[] getASMTransformerClass() 
	{
		//This will return the name of the class "com.limplungs.flatxp.EDClassTransformer"
		return new String[]{FlatXPClassTransformer.class.getName()};
	}

	@Override
	public String getModContainerClass() 
	{
		return FlatXPDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass() 
	{
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) 
	{
	
	}

	@Override
	public String getAccessTransformerClass() 
	{
		return null;
	}
}
