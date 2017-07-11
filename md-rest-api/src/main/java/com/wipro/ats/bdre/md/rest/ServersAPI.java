/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wipro.ats.bdre.md.rest;

import com.wipro.ats.bdre.md.api.base.MetadataAPIBase;
import com.wipro.ats.bdre.md.beans.table.Servers;
import com.wipro.ats.bdre.md.dao.ServersDAO;
import com.wipro.ats.bdre.md.rest.util.BindingResultError;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by arijit on 1/9/15.
 */
@Controller
@RequestMapping("/servers")


public class ServersAPI extends MetadataAPIBase {
    private static final Logger LOGGER = Logger.getLogger(ServersAPI.class);
    @Autowired
    ServersDAO serversDAO;

    /**
     * This method calls proc GetServers and fetches a record corresponding to the serverId passed.
     *
     * @param serverId
     * @return restWrapper It contains an instance of Servers corresponding to the serverId passed.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)

    @ResponseBody
    public RestWrapper get(
            @PathVariable("id") Integer serverId, Principal principal
    ) {

        RestWrapper restWrapper = null;
        try {
            com.wipro.ats.bdre.md.dao.jpa.Servers jpaServers = serversDAO.get(serverId);
            Servers server = new Servers();
            if (jpaServers != null) {
                server.setServerId(jpaServers.getServerId());
                server.setServerName(jpaServers.getServerName());
                server.setServerType(jpaServers.getServerType());
                server.setLoginUser(jpaServers.getLoginUser());
                server.setLoginPassword(jpaServers.getLoginPassword());
                server.setServerIp(jpaServers.getServerIp());
                server.setServerMetaInfo(jpaServers.getServerMetainfo());
                server.setSshPrivateKey(jpaServers.getSshPrivateKey());
            }

            restWrapper = new RestWrapper(server, RestWrapper.OK);
            LOGGER.info("Record with ID:" + serverId + " selected from Servers by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc DeleteServers and deletes an entry corresponding to the serverId paased.
     *
     * @param serverId
     * @return nothing.
     */
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)

    @ResponseBody
    public RestWrapper delete(
            @PathVariable("id") Integer serverId, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            serversDAO.delete(serverId);
            restWrapper = new RestWrapper(null, RestWrapper.OK);
            LOGGER.info("Record with ID:" + serverId + " deleted from Servers by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc ListServers and returns a list of instances of servers.
     *
     * @param
     * @return restWrapper It contains list of instances og servers.
     */
    @RequestMapping(value = {"", "/"}, method = RequestMethod.GET)


    @ResponseBody
    public RestWrapper list(@RequestParam(value = "page", defaultValue = "0") int startPage,
                     @RequestParam(value = "size", defaultValue = "10") int pageSize, Principal principal) {

        RestWrapper restWrapper = null;
        try {
            Integer counter=serversDAO.totalRecordCount();
            List<com.wipro.ats.bdre.md.dao.jpa.Servers> jpaServersList = serversDAO.list(startPage, pageSize);
            List<Servers> servers = new ArrayList<Servers>();
            for (com.wipro.ats.bdre.md.dao.jpa.Servers server : jpaServersList) {
                Servers returnServer = new Servers();
                returnServer.setServerId(server.getServerId());
                returnServer.setServerName(server.getServerName());
                returnServer.setServerType(server.getServerType());
                returnServer.setLoginUser(server.getLoginUser());
                returnServer.setLoginPassword(server.getLoginPassword());
                returnServer.setServerMetaInfo(server.getServerMetainfo());
                returnServer.setServerIp(server.getServerIp());
                returnServer.setSshPrivateKey(server.getSshPrivateKey());

                returnServer.setCounter(counter);
                servers.add(returnServer);
            }
            restWrapper = new RestWrapper(servers, RestWrapper.OK);
            LOGGER.info("All records listed from Servers by User:" + principal.getName());

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * This method calls proc UpdateServers and updates the values of the instance passed.
     *
     * @param server        Instance of Servers.
     * @param bindingResult
     * @return restWrapper Updated instance of Servers.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.POST)

    @ResponseBody
    public RestWrapper update(@ModelAttribute("servers")
                       @Valid Servers server, BindingResult bindingResult, Principal principal) {
        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.Servers jpaServers = new com.wipro.ats.bdre.md.dao.jpa.Servers();
            jpaServers.setServerId(server.getServerId());
            jpaServers.setServerName(server.getServerName());
            jpaServers.setServerType(server.getServerType());
            jpaServers.setServerIp(server.getServerIp());
            jpaServers.setSshPrivateKey(server.getSshPrivateKey());
            jpaServers.setLoginUser(server.getLoginUser());
            jpaServers.setLoginPassword(server.getLoginPassword());
            jpaServers.setServerMetainfo(server.getServerMetaInfo());
            serversDAO.update(jpaServers);
            restWrapper = new RestWrapper(server, RestWrapper.OK);
            LOGGER.info("Record with ID:" + server.getServerId() + " updated in Servers by User:" + principal.getName() + server);

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    /**
     * Thsi method calls proc InsertServers and adds a record of servers.
     *
     * @param server        Instance of Servers.
     * @param bindingResult
     * @return restWrapper It contains an instance of Servers added.
     */
    @RequestMapping(value = {"/", ""}, method = RequestMethod.PUT)

    @ResponseBody
    public RestWrapper insert(@ModelAttribute("servers")
                       @Valid Servers server, BindingResult bindingResult, Principal principal) {

        RestWrapper restWrapper = null;
        if (bindingResult.hasErrors()) {
            BindingResultError bindingResultError = new BindingResultError();
            return bindingResultError.errorMessage(bindingResult);
        }
        try {
            com.wipro.ats.bdre.md.dao.jpa.Servers jpaServers = new com.wipro.ats.bdre.md.dao.jpa.Servers();
            jpaServers.setServerId(server.getServerId());
            jpaServers.setServerName(server.getServerName());
            jpaServers.setServerType(server.getServerType());
            jpaServers.setServerIp(server.getServerIp());
            jpaServers.setSshPrivateKey(server.getSshPrivateKey());
            jpaServers.setLoginUser(server.getLoginUser());
            jpaServers.setLoginPassword(server.getLoginPassword());
            jpaServers.setServerMetainfo(server.getServerMetaInfo());
            Integer serversId = serversDAO.insert(jpaServers);
            server.setServerId(serversId);
            restWrapper = new RestWrapper(server, RestWrapper.OK);
            LOGGER.info("Record with ID:" + server.getServerId() + " inserted in Servers by User:" + principal.getName() + server);

        } catch (Exception e) {
            LOGGER.error(e);
            restWrapper = new RestWrapper(e.getMessage(), RestWrapper.ERROR);
        }
        return restWrapper;
    }

    @Override
    public Object execute(String[] params) {
        return null;
    }
}
