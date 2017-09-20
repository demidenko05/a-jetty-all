package org.beigesoft.afactory;

/*
 * Copyright (c) 2015-2017 Beigesoft ™
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
 * <pre>
 * Simple factory that create a request(or) scoped bean according
 * parameters.
 * </pre>
 *
 * @author Yury Demidenko
 * @param <M> type of created bean
 * @param <P> type of parameter
 **/
public interface IFactoryParam<M, P> {

  /**
   * <p>Create a bean with abstract params.</p>
   * @param pParam parameter
   * @throws Exception - an exception
   * @return M request(or) scoped bean
   */
  M create(P pParam) throws Exception;
}
