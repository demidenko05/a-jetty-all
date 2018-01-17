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

/**
 * <p>Abstraction of Bootstrap interface.</p>
 *
 * @author Yury Demidenko
 */
public interface IBootStrapIFace {

  /**
   * <p>Refresh user interface.</p>
   **/
  void refreshUi();

  /**
   * <p>Show error message.</p>
   * @param pError message
   **/
  void showError(String pError);
}
