/****************************************************************************
**
** Copyright (C) 1992-2009 Nokia. All rights reserved.
**
** This file is part of Qt Jambi.
**
** $BEGIN_LICENSE$
** GNU Lesser General Public License Usage
** This file may be used under the terms of the GNU Lesser
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
** $END_LICENSE$

**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
****************************************************************************/

package com.trolltech.unittests;

import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.trolltech.qt.QNoNativeResourcesException;
import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.core.QEventLoop;
import com.trolltech.qt.core.QMessageHandler;
import com.trolltech.qt.gui.QApplication;

import com.trolltech.autotests.QtJambiUnittestTools;
import com.trolltech.autotests.Utils;

public abstract class QApplicationTest extends QSignalEmitter {
    public static class MyQMessageHandler extends QMessageHandler {
        @Override
        public void debug(String message) {
            System.out.println("DEBUG: " + message);
            System.out.flush();
        }

        @Override
        public void warning(String message) {
            System.out.println("WARN: " + message);
            System.out.flush();
        }

        @Override
        public void critical(String message) {
            System.out.println("CRIT: " + message);
            System.out.flush();
        }

        @Override
        public void fatal(String message) {
            System.out.println("FATAL: " + message);
            System.out.flush();
        }
    }

    private static QMessageHandler myQMessageHandler;

    @BeforeClass
    public static void testInitialize() throws Exception {
        Utils.println(2, "QApplicationTest.testInitialize(): begin");
        QApplication.initialize(new String[] {});
        Utils.setupNativeResourceThread();
        myQMessageHandler = new MyQMessageHandler();
        QMessageHandler.installMessageHandler(myQMessageHandler);
        Utils.println(2, "QApplicationTest.testInitialize(): done");
    }

    @AfterClass
    public static void testDispose() throws Exception {
        Utils.println(2, "QApplicationTest.testDispose(): begin");
        if(Utils.releaseNativeResources() > 0) {
            System.gc();
            System.runFinalization();
        }
        System.gc();
        System.runFinalization();

        // We are attempting to reach a point here where memory should be reclaimed
        // except for anything linked to QApplication.

        QApplication app = QApplication.instance();
        try {
            Utils.println(3, "QApplicationTest.testDispose(): QApplication-only  app="+app);
        } catch(QNoNativeResourcesException e) {
            Utils.println(3, "QApplicationTest.testDispose(): QApplication-only  com.trolltech.qt.QNoNativeResourcesException: app="+e.getMessage());
        }

        System.err.flush();
        System.out.flush();

        if(app != null) {
            try {
                QApplication.processEvents();	// NPE
            } catch(Throwable t) {
                t.printStackTrace();
            }
            QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);	// NPE
        }
        QApplication.quit();
        app = QApplication.instance();
        if(app != null) {
            QApplication.processEvents();
            QApplication.processEvents(QEventLoop.ProcessEventsFlag.DeferredDeletion);
            app.dispose();
            try {
                Utils.println(3, "QApplicationTest.testDispose(): done  app="+app);
            } catch(QNoNativeResourcesException e) {
                Utils.println(3, "QApplicationTest.testDispose(): done  com.trolltech.qt.QNoNativeResourcesException: app="+e.getMessage());
            }
            app = null;		// kill hard-reference
        }
        if(myQMessageHandler != null) {
            QMessageHandler.removeMessageHandler(myQMessageHandler);
            myQMessageHandler = null;
        }
        System.gc();
        System.runFinalization();

        // We are attempting to reach a point here where memory should be reclaimed
        // including that which was linked to QApplication

        Utils.println(3, "QApplicationTest.testDispose(): shutdown PRE");
        QApplication.shutdown();
        Utils.println(3, "QApplicationTest.testDispose(): shutdown POST");

        QtJambiUnittestTools.getObjectCount(1, 0);  // fflush(stdout)
        QtJambiUnittestTools.getObjectCount(2, 0);  // fflush(stderr)
        int objectCount = QtJambiUnittestTools.getObjectCount(3, 0);  // QtJambiLink::QtJambiLink_dump()
        QtJambiUnittestTools.getObjectCount(2, 0);  // fflush(stderr)
        Utils.println(3, "QApplicationTest.testDispose(): end objectCount="+objectCount);

        if(objectCount == 0)
            return;  // optimization due to class loading causing some references to be set

        QtJambiUnittestTools.clearStaticReferences();
        System.gc();
        System.runFinalization();
        System.gc();
        System.runFinalization();

        QtJambiUnittestTools.getObjectCount(1, 0);  // fflush(stdout)
        QtJambiUnittestTools.getObjectCount(2, 0);  // fflush(stderr)
        objectCount = QtJambiUnittestTools.getObjectCount(3, 0);  // QtJambiLink::QtJambiLink_dump()
        QtJambiUnittestTools.getObjectCount(2, 0);  // fflush(stderr)
        Utils.println(3, "QApplicationTest.testDispose(): end objectCount="+objectCount);
    }
}
