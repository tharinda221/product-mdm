/**
 *  Copyright (c) 2014, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.wso2.carbon.device.mgt.core;

import org.wso2.carbon.device.mgt.common.Device;
import org.wso2.carbon.device.mgt.common.DeviceIdentifier;
import org.wso2.carbon.device.mgt.common.DeviceManagementException;
import org.wso2.carbon.device.mgt.common.OperationManager;
import org.wso2.carbon.device.mgt.common.spi.DeviceManagerService;
import org.wso2.carbon.device.mgt.core.config.DeviceManagementConfig;
import org.wso2.carbon.device.mgt.core.dao.DeviceDAO;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOException;
import org.wso2.carbon.device.mgt.core.dao.DeviceManagementDAOFactory;
import org.wso2.carbon.device.mgt.core.dao.DeviceTypeDAO;
import org.wso2.carbon.device.mgt.core.dao.util.DeviceManagementDAOUtil;

import java.util.List;

public class DeviceManagerImpl implements DeviceManager {

	private DeviceDAO deviceDAO;
	private DeviceTypeDAO deviceTypeDAO;
	private DeviceManagementConfig config;
	private DeviceManagementRepository pluginRepository;

	public DeviceManagerImpl(DeviceManagementConfig config,
	                         DeviceManagementRepository pluginRepository) {
		this.config = config;
		this.pluginRepository = pluginRepository;
		this.deviceDAO = DeviceManagementDAOFactory.getDeviceDAO();
		this.deviceTypeDAO = DeviceManagementDAOFactory.getDeviceTypeDAO();
	}

	@Override
	public boolean enrollDevice(Device device) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(device.getType());
		boolean status = dms.enrollDevice(device);
		try {
			this.getDeviceTypeDAO().getDeviceType();
			org.wso2.carbon.device.mgt.core.dto.Device deviceDto =
					DeviceManagementDAOUtil.convertDevice(
							device);

			Integer deviceTypeId =
					this.getDeviceTypeDAO().getDeviceTypeIdByDeviceTypeName(device.getType());
			deviceDto.setDeviceType(deviceTypeId);
			this.getDeviceDAO().addDevice(deviceDto);
		} catch (DeviceManagementDAOException e) {
			throw new DeviceManagementException("Error occurred while enrolling the device '" +
			                                    device.getId() + "'", e);
		}
		return status;
	}

	@Override
	public boolean modifyEnrollment(Device device) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(device.getType());
		boolean status = dms.modifyEnrollment(device);
		try {
			this.getDeviceDAO().updateDevice(DeviceManagementDAOUtil.convertDevice(device));
		} catch (DeviceManagementDAOException e) {
			throw new DeviceManagementException("Error occurred while modifying the device '" +
			                                    device.getId() + "'", e);
		}
		return status;
	}

	@Override
	public boolean disenrollDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(deviceId.getType());
		return dms.disenrollDevice(deviceId);
	}

	@Override
	public boolean isEnrolled(DeviceIdentifier deviceId) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(deviceId.getType());
		return dms.isEnrolled(deviceId);
	}

	@Override
	public boolean isActive(DeviceIdentifier deviceId) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(deviceId.getType());
		return dms.isActive(deviceId);
	}

	@Override
	public boolean setActive(DeviceIdentifier deviceId, boolean status)
			throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(deviceId.getType());
		return dms.setActive(deviceId, status);
	}

	@Override
	public List<Device> getAllDevices(String type) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(type);
		return dms.getAllDevices();
	}

	@Override
	public Device getDevice(DeviceIdentifier deviceId) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(deviceId.getType());
		return dms.getDevice(deviceId);
	}

	@Override
	public boolean updateDeviceInfo(Device device) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(device.getType());
		return dms.updateDeviceInfo(device);
	}

	@Override
	public boolean setOwnership(DeviceIdentifier deviceId, String ownershipType)
			throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(deviceId.getType());
		return dms.setOwnership(deviceId, ownershipType);
	}

	public OperationManager getOperationManager(String type) throws DeviceManagementException {
		DeviceManagerService dms =
				this.getPluginRepository().getDeviceManagementProvider(type);
		return dms.getOperationManager();
	}

	public DeviceDAO getDeviceDAO() {
		return deviceDAO;
	}

	public DeviceTypeDAO getDeviceTypeDAO() {
		return deviceTypeDAO;
	}

	public DeviceManagementRepository getPluginRepository() {
		return pluginRepository;
	}

}