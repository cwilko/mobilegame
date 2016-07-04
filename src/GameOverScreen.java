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


class GameOverScreen
    extends Canvas
{
    private final SheepdogMIDlet midlet;


    GameOverScreen(SheepdogMIDlet midlet)
    {
        super();
        this.midlet = midlet;
        setFullScreenMode(true);

        midlet.flashBacklight(10000);    // 1 second
    }


    public void paint(Graphics g)
    {
        int width = getWidth();
        int height = getHeight();

        // clear screen to green
        g.setColor(0x0000FF00);
        g.fillRect(0, 0, width, height);

        // Write message. We use a trick to make outlined text: we draw it
        // offset one pixel to the top, bottom, left & right in white, then
        // centred in black.
        g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,
                               Font.STYLE_BOLD,
                               Font.SIZE_MEDIUM));
        int centerX = width / 2;
        int centerY = height / 2;
        g.setColor(0x00FFFFFF);   // white
        drawText(g, centerX, centerY - 1);
        drawText(g, centerX, centerY + 1);
        drawText(g, centerX - 1, centerY);
        drawText(g, centerX + 1, centerY);
        g.setColor(0x00000000);   // black
        drawText(g, centerX, centerY);
    }


    private void drawText(Graphics g, int centerX, int centerY)
    {
        int fontHeight = g.getFont().getHeight();
        int textHeight = 3 * fontHeight;
        int topY = centerY - textHeight / 2;

        g.drawString("GAME OVER!",
                     centerX,
                     topY,
                     Graphics.HCENTER | Graphics.TOP);
    }
    

    public void keyPressed(int keyCode)
    {
        midlet.gameOverDone();
    }
}
