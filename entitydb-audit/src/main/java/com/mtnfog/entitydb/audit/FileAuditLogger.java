/**
 * Copyright © 2016 Mountain Fog, Inc. (support@mtnfog.com)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 *
 * For commercial licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.audit;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entitydb.model.audit.AuditAction;
import com.mtnfog.entitydb.model.audit.AuditLogger;

public class FileAuditLogger implements AuditLogger {
	
	private static final Logger LOGGER = LogManager.getLogger(AuditLogger.class);

	private File file;
	
	/**
	 * Creates a new file-based audit logger.
	 * @param fileName THe audit log file name.
	 */
	public FileAuditLogger(String fileName) {
		
		file = new File(fileName);
		
	}
	
	/**
	 * Creates a new file-based audit logger that uses a temporary file.
	 * @throws IOException Thrown if a temporary file cannot be created.
	 */
	public FileAuditLogger() throws IOException {
		
		file = File.createTempFile("audit", "log");
		
		LOGGER.info("Logging audit events to temporary file: {}", file.getAbsolutePath());
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean audit(String entityId, long timestamp, String userIdentifier, AuditAction auditAction, String entityDbId) {

		final String data = String.format("\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"", entityId, timestamp, userIdentifier, auditAction.toString(), entityDbId);
		
		try {
		
			FileUtils.writeStringToFile(file, data, true);
			
		} catch (IOException ex) {
			
			LOGGER.error("Unable to audit event.", ex);
			
		}
		
		return true;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean audit(String query, long timestamp, String userIdentifier, String entityDbId) {
		
		final String data = String.format("\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"\t\"%s\"", query, timestamp, userIdentifier, AuditAction.QUERY, entityDbId);
		
		try {
		
			FileUtils.writeStringToFile(file, data, true);
			
		} catch (IOException ex) {
			
			LOGGER.error("Unable to audit event.", ex);
			
		}
		
		return true;
		
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void close() {

		// Nothing to do here.
		
	}

}