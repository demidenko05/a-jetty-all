package org.beigesoft.afactory;

/*
 * Beigesoft ™
 *
 * Licensed under the Apache License, Version 2.0
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
