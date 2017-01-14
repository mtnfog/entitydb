/**
 * Copyright © 2017 Mountain Fog, Inc. (support@mtnfog.com)
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
 * For proprietary licenses contact support@mtnfog.com or visit http://www.mtnfog.com.
 */
package com.mtnfog.entitydb.eql.model;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mtnfog.entity.Entity;

/**
 * A query to be executed by an {@link EntityStore}. At least one property
 * of this class must be provided or all stored entities will be returned
 * by the query. If the {@link EntityOrder} is not set the default sort
 * order is the entity text. Searches are not case-sensitive by default.
 * 
 * @author Mountain Fog, Inc.
 *
 */
public class EntityQuery {
	
	private static final Logger LOGGER = LogManager.getLogger(EntityQuery.class);

	private ConfidenceRange confidenceRange;
	
	private String text;
	private String notText;
	
	private String type;
	private String notType;
	
	private String languageCode;
	private String notLanguageCode;
		
	private String context;
	private String notContext;
	
	private String documentId;
	private String notDocumentId;
		
	private String uri;
	private String notUri;		
	
	private int limit = 25;
	private int offset = 0;

	private List<EntityEnrichmentFilter> entityEnrichmentFilters;
	private EntityOrder entityOrder = EntityOrder.ID;
	private SortOrder sortOrder = SortOrder.DESC;
	
	public boolean isMatch(Entity entity) {
				
		boolean match = false;
		
		if(confidenceRange != null
				&& confidenceRange.getMinimum() <= entity.getConfidence()
				&& entity.getConfidence() <= confidenceRange.getMaximum()) {

				LOGGER.debug("Entity confidence: {}", entity.getConfidence());
				LOGGER.debug("Range: {} to {}", confidenceRange.getMinimum(), confidenceRange.getMaximum());
				
				match = true;
				
		}
		
		if(StringUtils.isNotEmpty(text)) {
			
			if(StringUtils.equalsIgnoreCase(text, entity.getText())) {
				
				match = true;
			
			} else {
				
				match = false;
				
			}
			
		}
		
		if(StringUtils.isNotEmpty(type)) {
			
			if(StringUtils.equalsIgnoreCase(text, entity.getType())) {
			
				match = true;
			
			} else {
				
				match = false;
				
			}
			
		}
		
		if(StringUtils.isNotEmpty(context)) {
			
			if(StringUtils.equalsIgnoreCase(context, entity.getContext())) {
			
				match = true;
			
			} else {
				
				match = false;
				
			}
			
		}
		
		if(StringUtils.isNotEmpty(documentId)) {
			
			if(StringUtils.equalsIgnoreCase(documentId, entity.getDocumentId())) {
			
				match = true;
			
			} else {
				
				match = false;
				
			}
			
		}
		
		if(StringUtils.isNotEmpty(uri)) {
			
			if(StringUtils.equalsIgnoreCase(uri, entity.getUri())) {
			
				match = true;
			
			} else {
				
				match = false;
				
			}
			
		}
		
		if(StringUtils.isNotEmpty(languageCode)) {
			
			if(StringUtils.equalsIgnoreCase(languageCode, entity.getLanguageCode())) {
			
				match = true;
			
			} else {
				
				match = false;
				
			}
			
		}
		
		return match;
		
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}
	
	/**
	 * Gets the confidence range for the query.
	 * @return The {@link ConfidenceRange}.
	 */
	public ConfidenceRange getConfidenceRange() {
		return confidenceRange;
	}
	
	/**
	 * Sets the confidence range for the query.
	 * @param confidenceRange The {@link ConfidenceRange}.
	 */
	public void setConfidenceRange(ConfidenceRange confidenceRange) {
		this.confidenceRange = confidenceRange;
	}
	
	/**
	 * Sets the confidence range for the query.
	 * @param minimum The minimum confidence value.
	 * @param maximum The maximum confidence value.
	 */
	public void setConfidenceRange(double minimum, double maximum) {		
		this.confidenceRange = new ConfidenceRange(minimum, maximum);		
	}

	/**
	 * Gets the entity text for the query.
	 * @return The entity text.
	 */
	public String getText() {
		return text;
	}

	/**
	 * Sets the entity text for the query. Refer to the implementation
	 * of the {@link EntityStore} to see if wildcard characters
	 * are allowed.
	 * @param text The entity text.
	 */
	public void setText(String text) {
		this.text = text;
	}

	/**
	 * The type of entities to be queried.
	 * @return The entity type.
	 */
	public String getType() {
		return type;
	}

	/**
	 * Set the type of entities to be queried.
	 * @param type The entity type.
	 */
	public void setType(String type) {
		this.type = type.toLowerCase();
	}

	/**
	 * Gets the sort {@link EntityOrder order}. If null the ordering is by the entity ID.
	 * @return The sort order.
	 */
	public EntityOrder getEntityOrder() {
		
		if(entityOrder == null) {
			return EntityOrder.ID;
		} else {
			return entityOrder;
		}
	}

	/**
	 * Sets the sort {@link EntityOrder order}.
	 * @param entityOrder The sort order.
	 */
	public void setEntityOrder(EntityOrder entityOrder) {
		this.entityOrder = entityOrder;
	}

	/**
	 * Gets the context.
	 * @return The context.
	 */
	public String getContext() {
		return context;
	}

	/**
	 * Sets the context.
	 * @param context The context.
	 */
	public void setContext(String context) {
		this.context = context;
	}

	/**
	 * Gets the list of entity enrichment filters for the query.
	 * @return A list of {@link EntityEnrichmentFilter}.
	 */
	public List<EntityEnrichmentFilter> getEntityEnrichmentFilters() {
		return entityEnrichmentFilters;
	}

	/**
	 * Sets the list of entity enrichment filters for the query.
	 * @param entityEnrichmentFilters A list of {@link EntityEnrichmentFilter}.
	 */
	public void setEntityEnrichmentFilters(List<EntityEnrichmentFilter> entityEnrichmentFilters) {
		this.entityEnrichmentFilters = entityEnrichmentFilters;
	}

	/**
	 * Gets the document ID.
	 * @return A document ID.
	 */
	public String getDocumentId() {
		return documentId;
	}

	/**
	 * Sets the document ID.
	 * @param documentId The document ID.
	 */
	public void setDocumentId(String documentId) {
		this.documentId = documentId;
	}

	/**
	 * Gets the limit of results to return.
	 * @return The limit of results to return.
	 */
	public int getLimit() {
		return limit;
	}

	/**
	 * Sets the limit of results to return.
	 * @param limit The limit of results to return.
	 */
	public void setLimit(int limit) {
		this.limit = limit;
	}

	/**
	 * Gets the offset for results.
	 * @return The offset for results.
	 */
	public int getOffset() {
		return offset;
	}

	/**
	 * Sets the offset for results.
	 * @param offset The offset for results.
	 */
	public void setOffset(int offset) {
		this.offset = offset;
	}

	/**
	 * Gets the URI.
	 * @return The URI.
	 */
	public String getUri() {
		return uri;
	}

	/**
	 * Sets the URI.
	 * @param uri The URI.
	 */
	public void setUri(String uri) {
		this.uri = uri;
	}

	/**
	 * Gets the language;
	 * @return The language.
	 */
	public String getLanguageCode() {
		return languageCode;
	}

	/**
	 * Sets the language.
	 * @param language The language.
	 */
	public void setLanguageCode(String languageCode) {
		this.languageCode = languageCode;
	}

	public String getNotContext() {
		return notContext;
	}

	public void setNotContext(String notContext) {
		this.notContext = notContext;
	}

	public String getNotDocumentId() {
		return notDocumentId;
	}

	public void setNotDocumentId(String notDocumentId) {
		this.notDocumentId = notDocumentId;
	}

	public String getNotText() {
		return notText;
	}

	public void setNotText(String notText) {
		this.notText = notText;
	}

	public String getNotType() {
		return notType;
	}

	public void setNotType(String notType) {
		this.notType = notType;
	}

	public String getNotUri() {
		return notUri;
	}

	public void setNotUri(String notUri) {
		this.notUri = notUri;
	}

	public String getNotLanguageCode() {
		return notLanguageCode;
	}

	public void setNotLanguageCode(String notLanguage) {
		this.notLanguageCode = notLanguage;
	}
	
	/**
	 * Gets the {@link SortOrder}. If null the sort order is descending.
	 * @return The {@link SortOrder}.
	 */
	public SortOrder getSortOrder() {
		
		if(sortOrder == null) {
			return SortOrder.DESC;
		} else {
			return sortOrder;
		}
		
	}

	/**
	 * Sets the {@link SortOrder}.
	 * @param sortOrder The {@link SortOrder}.
	 */
	public void setSortOrder(SortOrder sortOrder) {
		this.sortOrder = sortOrder;
	}
	
}