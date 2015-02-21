package ca.mcgill.cs.stg.jetuml.framework;

import java.util.Stack;

import ca.mcgill.cs.stg.jetuml.commands.Command;
import ca.mcgill.cs.stg.jetuml.commands.CompoundCommand;

public class UndoManager 
{
	private Stack<Command> aPastCommands; //the commands that haev been inputted and can be undone
	private Stack<Command> aUndoneCommands; //the commands that have been undone and can be redone
	private CompoundCommand aTrackingCommand; //used for many commands coming at once
	private boolean aTracking; //turned on to allow many things to be changed in one command
	private boolean holdChanges = false; //turned on while undoing or redoing to prevent duplication
	private GraphPanel aGraphPanel;
	
	public UndoManager(GraphPanel pPanel)
	{
		aGraphPanel = pPanel;
		aPastCommands = new Stack<Command>();
		aUndoneCommands = new Stack<Command>();
	}

	public void add(Command pCommand)
	{
		if(!holdChanges)
		{
			if(!aUndoneCommands.empty())
			{
				aUndoneCommands.clear();
			}
			if(aTracking)
			{
				aTrackingCommand.add(pCommand);
			}
			else
			{
				aPastCommands.push(pCommand);
			}
		}
	}

	public void undoCommand()
	{
		if(aPastCommands.empty())
		{
			return;
		}
		holdChanges = true;
		Command toUndo = aPastCommands.pop();
		toUndo.undo();
		aUndoneCommands.push(toUndo);
		holdChanges = false;
		aGraphPanel.repaint();
	}

	/**
	 * Pops most recent undone command and executes it
	 */
	void redoCommand()
	{
		holdChanges = true;
		if (aUndoneCommands.empty())
		{
			return;
		}
		Command toRedo = aUndoneCommands.pop();
		toRedo.execute();
		aPastCommands.push(toRedo);
		holdChanges = false;
		aGraphPanel.repaint();
	}

	/**
	 * Creates a compound command that all coming commands will be added to
	 * Used to many commands at once
	 */
	public void startTracking()
	{
		aTracking = true;
		aTrackingCommand = new CompoundCommand();
	}

	/**
	 * Finishes off the compound command and adds it to the stack
	 */
	public void endTracking()
	{
		aTracking = false;
		if(aTrackingCommand.size() > 0)
		{
			add(aTrackingCommand);
		}
	}

}