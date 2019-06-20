package org.fross.quote;

import com.diogonunes.jcdp.color.api.Ansi.FColor;

/**
 * Help(): Display the help page when users enters 'h' command.
 * 
 * @author michael.d.fross
 *
 */
public class Help {
	/**
	 * Display(): Prints help in color using the JCDP library in the output module.
	 */
	public static void Display() {
		Output.PrintColorln(FColor.YELLOW,
				"\n+----------------------------------------------------------------------+");
		Output.PrintColorln(FColor.YELLOW,
				"+              Quote Calculator v" + Main.VERSION + "  Help Document               +");
		Output.PrintColorln(FColor.YELLOW, "+----------------------------------------------------------------------+");
		Output.PrintColorln(FColor.WHITE, "            RPN is a simple reverse polish notation calculator");
		Output.PrintColorln(FColor.WHITE, "               https://bitbucket.org/frossm/rpn/src/default\n");

		Output.PrintColorln(FColor.YELLOW, "Command Line Options:");
		Output.PrintColorln(FColor.WHITE, " -l     Load saved named stack. Create the stack if it does not exist");
		Output.PrintColorln(FColor.WHITE, " -D     Start in debug mode.  Same as using the 'debug' command\n");

		Output.PrintColorln(FColor.YELLOW, "Operands:");
		Output.PrintColorln(FColor.WHITE, " +      Addition:  Add last two stack elements");
		Output.PrintColorln(FColor.WHITE, " -      Subtraction: Subtract last element from previous element");
		Output.PrintColorln(FColor.WHITE, " *      Multiplication: Muliply last two stack items");
		Output.PrintColorln(FColor.WHITE, " /      Division: Divide second to last item by last stack item");
		Output.PrintColorln(FColor.WHITE,
				" ^      Power:  Calculate second to last item to the power of the last item");
		Output.PrintColorln(FColor.WHITE,
				" %      Percent: Turn the last stack item into a percentage (multiplied by 0.01)");

		Output.PrintColorln(FColor.YELLOW, "Commands:");
		Output.PrintColorln(FColor.WHITE, " s[#] [#] Swap the last two elments in the stack or the elements provided");
		Output.PrintColorln(FColor.WHITE, " c        Clear everything from the visible stack");
		Output.PrintColorln(FColor.WHITE,
				" d[#]     Delete the last item in the stack or, optionally, the line number provided");
		Output.PrintColorln(FColor.WHITE, " f        Flip the sign of the last stack element");
		Output.PrintColorln(FColor.WHITE, " copy     Copy the item at the top of the stack");
		Output.PrintColorln(FColor.WHITE, " pi       Insert the value of PI onto the end of the stack");
		Output.PrintColorln(FColor.WHITE, " sqrt     Perform a square root on the last stack number\n");
		Output.PrintColorln(FColor.WHITE,
				" ss       Swap primary stack to secondary.  You can swap them back at a later time");
		Output.PrintColorln(FColor.WHITE, " load     Load a saved named stack. It will be created if it doesn't exist");
		Output.PrintColorln(FColor.WHITE, " debug    Toggle DEBUG mode on/off");
		Output.PrintColorln(FColor.WHITE, " ver      Display the current version");
		Output.PrintColorln(FColor.WHITE, " h|?      Show this help information.  Either key will work.");
		Output.PrintColorln(FColor.WHITE, " x        Exit Calculator\n");

		Output.PrintColorln(FColor.WHITE,
				"Note: You can place an operand at the end of a number and execute in one step.");
		Output.PrintColorln(FColor.WHITE,
				"For Example:  To add two numbers:   2 <enter> 3+ <enter>   will produce 5.\n");
	}
}