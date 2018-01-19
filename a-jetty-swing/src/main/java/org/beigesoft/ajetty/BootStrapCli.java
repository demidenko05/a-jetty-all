package org.beigesoft.ajetty;

/*
 * Copyright (c) 2018 Beigesoft ™
 *
 * Licensed under the GNU General Public License (GPL), Version 2.0
 * (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.en.html
 */

import java.io.Console;
import java.util.Date;

/**
 * <p>Command line interface to start A-Jetty.</p>
 *
 * @author Yury Demidenko
 */
public class BootStrapCli implements IBootStrapIFace {

  /**
   * <p>Main boot strap.</p>
   **/
  private BootStrapEmbeddedMain mainBootStrap;

  /**
   * <p>Is exit flag.</p>
   **/
  private boolean isExit = false;

  /**
   * <p>Show error message.</p>
   * @param pError message
   **/
  @Override
  public final void showError(final String pError) {
    Date dt = new Date();
    System.console().printf(dt.toString() + " "
      + this.mainBootStrap.getMsg("error") + ": " + pError + "\n");
  }

  /**
   * <p>Refresh user interface.</p>
   **/
  @Override
  public final void refreshUi() {
    Date dt = new Date();
    if (this.mainBootStrap.getIsActionPerforming()) {
      System.console().printf(dt.toString() + " "
        + this.mainBootStrap.getMsg("wait") + "\n");
    } else {
      if (this.mainBootStrap.getBootStrapEmbeddedHttps().getIsStarted()) {
        System.console().printf(dt.toString() + " "
            + this.mainBootStrap.getMsg("started") + " https://localhost:"
              + this.mainBootStrap.getPort() + "/bsa"
                + this.mainBootStrap.getPort() + " on A-Jetty #"
                  + this.mainBootStrap.getAjettyIn() + "\n");
      } else {
        System.console().printf(dt.toString() + " "
          + this.mainBootStrap.getMsg("stopped") + " on A-Jetty #"
            + this.mainBootStrap.getAjettyIn() + "\n");
      }
    }
  }

  /**
   * <p>Starts interface.</p>
   * @throws ExceptionStart ExceptionStart
   **/
  public final void startInterface() throws ExceptionStart {
    Console con = System.console();
    if (con == null) {
      throw new ExceptionStart("There is no console!!!");
    }
    CliThread ct = new CliThread();
    ct.start();
  }

  /**
   * <p>User interface logic.</p>
   **/
  public final void doWork() {
    if (this.mainBootStrap.getIsActionPerforming()) {
      long currDate = new Date().getTime();
      if (currDate - this.mainBootStrap.getLastActionStartDate() > 120000) {
        this.mainBootStrap.setIsActionPerforming(false);
        this.mainBootStrap.getLogger()
          .error(null, BootStrapCli.class, "Frozen!!!");
      }
    }
    if (!this.mainBootStrap.getIsActionPerforming()) {
      if (!this.mainBootStrap.getBootStrapEmbeddedHttps().getIsStarted()) {
        enterAjettyNumIfNeed();
        if (this.isExit) {
          return;
        }
        if (!this.mainBootStrap.getIsKeystoreCreated()) {
          enterKsPasswd();
          if (this.isExit) {
            return;
          }
          enterKsPasswdConf();
        } else if (this.mainBootStrap.getKsPassword() == null
          || this.mainBootStrap.getIsLastStartFail()) {
          enterKsPasswd();
        }
        if (this.isExit) {
          return;
        }
        enterPort();
        enterStartOrQuit();
      } else {
        enterStopOrQuit();
      }
    }
  }

  /**
   * <p>Enter Start or quit logic.</p>
   **/
  public final void enterStartOrQuit() {
    int attempts = 0;
    Console con = System.console();
    while (attempts++ < 10) {
      try {
        String wrd = con.readLine(
          this.mainBootStrap.getMsg("enter_ajetty_start"));
        if ("q".equals(wrd)) {
          this.isExit = true;
          return;
        } else if ("g".equals(wrd)) {
          this.mainBootStrap.setIsActionPerforming(true);
          this.mainBootStrap.startAjetty();
          return;
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * <p>Enter Stop or quit logic.</p>
   **/
  public final void enterStopOrQuit() {
    int attempts = 0;
    Console con = System.console();
    while (attempts++ < 10) {
      try {
        String wrd = con
          .readLine(this.mainBootStrap.getMsg("enter_ajetty_stop"));
        if ("q".equals(wrd)) {
          this.isExit = true;
          this.mainBootStrap.setIsActionPerforming(true);
          this.mainBootStrap.stopAjetty();
          return;
        } else if ("s".equals(wrd)) {
          this.mainBootStrap.setIsActionPerforming(true);
          this.mainBootStrap.stopAjetty();
          return;
        }
      } catch (ExceptionStart e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * <p>Enter A-Jetty number logic.</p>
   **/
  public final void enterAjettyNumIfNeed() {
    int attempts = 0;
    Console con = System.console();
    while (!this.isExit && this.mainBootStrap.getAjettyIn() == null) {
      try {
        String wrd = con.readLine(this.mainBootStrap.getMsg("enter_ajetty_sn"));
        if ("q".equals(wrd)) {
          this.isExit = true;
          return;
        }
        this.mainBootStrap.setAjettyIn(Integer.parseInt(wrd));
      } finally {
        if (attempts++ > 10) {
          this.isExit = true;
        }
      }
    }
  }

  /**
   * <p>Enter port logic.</p>
   **/
  public final void enterPort() {
    Console con = System.console();
    try {
      String port = con
        .readLine(this.mainBootStrap.getMsg("enter_ajetty_port"));
      if (port == null || port.length() == 0) {
        return;
      }
      this.mainBootStrap.setPort(Integer.parseInt(port));
    } catch (Exception e) {
      con.printf(this.mainBootStrap.getMsg("ajetty_port_def") + "\n");
    }
  }

  /**
   * <p>Enter key-store password logic.</p>
   **/
  public final void enterKsPasswd() {
    int attempts = 0;
    this.mainBootStrap.setKsPassword(null);
    Console con = System.console();
    while (!this.isExit && this.mainBootStrap.getKsPassword() == null) {
      try {
        char[] passwd = con
          .readPassword(this.mainBootStrap.getMsg("enter_ajetty_passwd"));
        if (passwd != null && passwd.length == 1 && passwd[0] == 'q') {
          this.isExit = true;
          return;
        }
        if (passwd != null) {
          this.mainBootStrap.setKsPassword(passwd);
        }
      } finally {
        if (attempts++ > 10) {
          this.isExit = true;
        }
      }
    }
  }

  /**
   * <p>Enter key-store confirm password logic.</p>
   **/
  public final void enterKsPasswdConf() {
    int attempts = 0;
    this.mainBootStrap.setKsPasswordConf(null);
    Console con = System.console();
    while (!this.isExit && this.mainBootStrap.getKsPasswordConf() == null) {
      try {
        char[] passwd = con
          .readPassword(this.mainBootStrap.getMsg("enter_ajetty_passwd_conf"));
        if (passwd != null && passwd.length == 1 && passwd[0] == 'q') {
          this.isExit = true;
          return;
        }
        if (passwd != null) {
          this.mainBootStrap.setKsPasswordConf(passwd);
        }
      } finally {
        if (attempts++ > 10) {
          this.isExit = true;
        }
      }
    }
  }

  //Inner classes:
  /**
   * <p>Command line interface thread.</p>
   */
  private class CliThread extends Thread {

    @Override
    public void run() {
      while (!BootStrapCli.this.isExit) {
        BootStrapCli.this.doWork();
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
          e.printStackTrace();
        }
      }
    }
  };

  //SGS:
  /**
   * <p>Getter for mainBootStrap.</p>
   * @return BootStrapEmbeddedMain
   **/
  public final BootStrapEmbeddedMain getMainBootStrap() {
    return this.mainBootStrap;
  }

  /**
   * <p>Setter for mainBootStrap.</p>
   * @param pMainBootStrap reference
   **/
  public final void setMainBootStrap(
    final BootStrapEmbeddedMain pMainBootStrap) {
    this.mainBootStrap = pMainBootStrap;
  }
}
