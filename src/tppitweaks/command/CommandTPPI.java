package tppitweaks.command;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.network.packet.Packet250CustomPayload;
import net.minecraft.util.ChatMessageComponent;
import tppitweaks.TPPITweaks;
import tppitweaks.config.ConfigurationHandler;
import tppitweaks.lib.Reference;
import tppitweaks.util.TxtParser;
import cpw.mods.fml.common.network.PacketDispatcher;
import cpw.mods.fml.common.network.Player;

public class CommandTPPI extends CommandBase
{

	private static HashSet<String> validCommands = new HashSet<String>();
	
	/** First index is list, rest are mod names **/
	private static ArrayList<String> supportedModsAndList = new ArrayList<String>();

	public static void initValidCommandArguments(InputStream file)
	{
		validCommands.add("download");
		validCommands.add("mods");
		
		supportedModsAndList.add("list");
		
		supportedModsAndList.addAll(TxtParser.getSupportedMods(file));
	}

	private boolean isValidArgument(String s)
	{
		return validCommands.contains(s);
	}

	@Override
	public String getCommandName()
	{
		return "tppi";
	}

	@Override
	public String getCommandUsage(ICommandSender icommandsender)
	{
		return "tppi <arg>";
	}
	
	@Override
	public int getRequiredPermissionLevel()
	{
		return 0;
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public List addTabCompletionOptions(ICommandSender command, String[] par2ArrayOfStr)
    {			
		if (par2ArrayOfStr.length == 1)
		{
			return getListOfStringsMatchingLastWord(par2ArrayOfStr, (String[]) validCommands.toArray(new String[validCommands.size()]));
		}
		else if (par2ArrayOfStr.length == 2)
		{
			System.out.println(par2ArrayOfStr[1]);
			if (par2ArrayOfStr[0].equals("mods"))
				return getListOfStringsMatchingLastWord(par2ArrayOfStr, supportedModsAndList.toArray(new String[supportedModsAndList.size()]));
			else return null;
		}
		else return null;
    }

	@Override
	public void processCommand(ICommandSender icommandsender, String[] astring)
	{
		System.out.println(supportedModsAndList.toString());
		if (astring.length > 0 && isValidArgument(astring[0]))
		{
			if (astring[0].equals("download"))
			{
				if (!processCommandDownload(icommandsender, astring))
					System.err.println("Invalid Player");
			}
			else if (astring[0].equals("mods"))
			{
				processCommandMods(icommandsender, astring);	
			}
			
		}
		else if (astring.length <= 0)
			icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("Proper Usage: /tppi <arg>"));
		else
			icommandsender.sendChatToPlayer(new ChatMessageComponent().addText("Invalid Argument"));
	}

	private boolean processCommandMods(ICommandSender command, String[] args)
	{
		if (args.length == 2)
		{
			if (args[1].equals("list"))
			{
				listMods(command);
				return true;
			}
			else if (supportedModsAndList.contains(args[1]))
				giveModBook(args[1], command);
			else
				command.sendChatToPlayer(new ChatMessageComponent().addText("Invalid Argument After: mods"));
		}
		else
			command.sendChatToPlayer(new ChatMessageComponent().addText("Proper Usage: /tppi mods <arg>"));
		
		return false;
	}


	private boolean processCommandDownload(ICommandSender command, String[] args)
	{
		Packet250CustomPayload packet = new Packet250CustomPayload();

		packet.channel = Reference.CHANNEL;

		byte[] bytes = { (byte) 0 };
		boolean showGui = command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()) != null;

		if (showGui)
		{
			packet.length = 1;
			packet.data = bytes;
			PacketDispatcher.sendPacketToPlayer(packet, (Player) command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()));
			return true;
		}
		
		return false;
	}
	
	private void listMods(ICommandSender icommandsender)
	{
		String s = "";
		
		for (int i = 1; i < supportedModsAndList.size(); i++)
		{
			s += supportedModsAndList.get(i);
			if (i < supportedModsAndList.size() - 1)
				s += ", ";
			if (i % 4 == 0)
				s += "\n";
		}
		
		icommandsender.sendChatToPlayer(new ChatMessageComponent().addText(s));		
	}
	
	private void giveModBook(String modName, ICommandSender command)
	{
		ItemStack stack = new ItemStack(Item.writtenBook);
		
		stack.setTagInfo("author", new NBTTagString("author", ConfigurationHandler.bookAuthor));
		stack.setTagInfo("title", new NBTTagString("title", "Info on " + modName));

		NBTTagCompound nbttagcompound = stack.getTagCompound();
		NBTTagList bookPages = new NBTTagList("pages");
		
		ArrayList<String> pages = TxtParser.parseFileMods(TPPITweaks.class.getResourceAsStream("/assets/tppitweaks/lang/SupportedMods.txt"), modName);
		
		if (pages.get(0).startsWith("<") && pages.get(0).endsWith(">"))
		{
			command.sendChatToPlayer(new ChatMessageComponent().addText(pages.get(0).substring(1, pages.get(0).length() - 1)));
			return;
		}
		
		for (int i = 0; i < pages.size(); i++)
		{
			bookPages.appendTag(new NBTTagString("" + i, pages.get(i)));
		}

		nbttagcompound.setTag("pages", bookPages);		

		if (!command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).inventory.addItemStackToInventory(stack))
			command.getEntityWorld().getPlayerEntityByName(command.getCommandSenderName()).entityDropItem(stack, 0);
	}
	
	
}