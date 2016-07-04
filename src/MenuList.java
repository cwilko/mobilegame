// Copyright 2003 Nokia Corporation.
//
// THIS SOURCE CODE IS PROVIDED 'AS IS', WITH NO WARRANTIES WHATSOEVER,
// EXPRESS OR IMPLIED, INCLUDING ANY WARRANTY OF MERCHANTABILITY, FITNESS
// FOR ANY PARTICULAR PURPOSE, OR ARISING FROM A COURSE OF DEALING, USAGE
// OR TRADE PRACTICE, RELATING TO THE SOURCE CODE OR ANY WARRANTY OTHERWISE
// ARISING OUT OF ANY PROPOSAL, SPECIFICATION, OR SAMPLE AND WITH NO
// OBLIGATION OF NOKIA TO PROVIDE THE LICENSEE WITH ANY MAINTENANCE OR
// SUPPORT. FURTHERMORE, NOKIA MAKES NO WARRANTY THAT EXERCISE OF THE
// RIGHTS GRANTED HEREUNDER DOES NOT INFRINGE OR MAY NOT CAUSE INFRINGEMENT
// OF ANY PATENT OR OTHER INTELLECTUAL PROPERTY RIGHTS OWNED OR CONTROLLED
// BY THIRD PARTIES
//
// Furthermore, information provided in this source code is preliminary,
// and may be changed substantially prior to final release. Nokia Corporation
// retains the right to make changes to this source code at
// any time, without notice. This source code is provided for informational
// purposes only.
//
// Nokia and Nokia Connecting People are registered trademarks of Nokia
// Corporation.
// Java and all Java-based marks are trademarks or registered trademarks of
// Sun Microsystems, Inc.
// Other product and company names mentioned herein may be trademarks or
// trade names of their respective owners.
//
// A non-exclusive, non-transferable, worldwide, limited license is hereby
// granted to the Licensee to download, print, reproduce and modify the
// source code. The licensee has the right to market, sell, distribute and
// make available the source code in original or modified form only when
// incorporated into the programs developed by the Licensee. No other
// license, express or implied, by estoppel or otherwise, to any other
// intellectual property rights is granted herein.


// unnamed package

import javax.microedition.lcdui.*;


class MenuList
    extends List
    implements CommandListener
{
    private SheepdogMIDlet midlet;
    private Command exitCommand;
    private boolean gameActive = false;


    MenuList(SheepdogMIDlet midlet)
    {
        super("Cat Attack", List.IMPLICIT);
        this.midlet = midlet;

        append("New game", null);
        append("High score", null);
        append("Instructions", null);

        exitCommand = new Command("Exit", Command.EXIT, 1);
        addCommand(exitCommand);
        
        setCommandListener(this);
    }


    void setGameActive(boolean active)
    {
        if (active && !gameActive)
        {
            gameActive = true;
            insert(0, "Continue", null);
        }
        else if (!active && gameActive)
        {
            gameActive = false;
            delete(0);
        }
    }


    public void commandAction(Command c, Displayable d)
    {
        if (c == List.SELECT_COMMAND)
        {
            int index = getSelectedIndex();
            if (index != -1)  // should never be -1
            {
                if (!gameActive)
                {
                    index++;
                }
                switch (index)
                {
                case 0:   // Continue
                    midlet.menuListContinue();
                    break;
                case 1:   // New game
                    midlet.menuListNewGame();
                    break;
                case 2:   // High score
                    midlet.menuListHighScore();
                    break;
                case 3:
                    midlet.menuListInstructions();
                    break;
                default:
                    // can't happen
                    break;
                }
            }
        }
        else if (c == exitCommand)
        {
            midlet.menuListQuit();
        }
    }
}
