package com.mtnfog.entitydb.audit;

public abstract class AbstractAuditLogger {

	protected String systemId;
	
	public AbstractAuditLogger(String systemId) {
		this.systemId = systemId;
	}
	
}
