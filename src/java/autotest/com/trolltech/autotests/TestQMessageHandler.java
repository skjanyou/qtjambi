/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** ** $BEGIN_LICENSE$
** Commercial Usage
** Licensees holding valid Qt Commercial licenses may use this file in
** accordance with the Qt Commercial License Agreement provided with the
** Software or, alternatively, in accordance with the terms contained in
** a written agreement between you and Nokia.
** 
** GNU Lesser General Public License Usage
** Alternatively, this file may be used under the terms of the GNU Lesser
** General Public License version 2.1 as published by the Free Software
** Foundation and appearing in the file LICENSE.LGPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU Lesser General Public License version 2.1 requirements
** will be met: http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html.
** 
** In addition, as a special exception, Nokia gives you certain
** additional rights. These rights are described in the Nokia Qt LGPL
** Exception version 1.0, included in the file LGPL_EXCEPTION.txt in this
** package.
** 
** GNU General Public License Usage
** Alternatively, this file may be used under the terms of the GNU
** General Public License version 3.0 as published by the Free Software
** Foundation and appearing in the file LICENSE.GPL included in the
** packaging of this file.  Please review the following information to
** ensure the GNU General Public License version 3.0 requirements will be
** met: http://www.gnu.org/copyleft/gpl.html.
** 
** If you are unsure which license is appropriate for your use, please
** contact the sales department at qt-sales@nokia.com.
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.autotests;

import com.trolltech.autotests.generated.*;
import com.trolltech.qt.core.*;
import com.trolltech.qt.gui.QApplication;

import org.junit.*;

import static org.junit.Assert.*;

public class TestQMessageHandler extends QMessageHandler {



    @Override
    public void debug(String message) {
        lastDebug = message;
    }

    @Override
    public void warning(String message) {
        lastWarning = message;
    }

    @Override
    public void critical(String message) {
        lastCritical = message;
    }

    @Override
    public void fatal(String message) {

    }

    @BeforeClass
    public static void testInitialize() throws Exception {

        QApplication.initialize(new String[] {});
    }

    @AfterClass
    public static void testDispose() throws Exception {
        QApplication.quit();
        QApplication.instance().dispose();
    }

    @Test
    public void test() {
        QMessageHandler.installMessageHandler(this);

        MessageHandler.sendDebug("debug sent");
        assertEquals(lastDebug, "debug sent");

        MessageHandler.sendWarning("warning sent");
        assertEquals(lastWarning, "warning sent");

        MessageHandler.sendCritical("critical sent");
        assertEquals(lastCritical, "critical sent");

        // Want to send fatal, but that will shut down app...
    }

    private String lastDebug;
    private String lastWarning;
    private String lastCritical;

    public static void main(String args[]) {
        org.junit.runner.JUnitCore.main(TestQMessageHandler.class.getName());
    }
}