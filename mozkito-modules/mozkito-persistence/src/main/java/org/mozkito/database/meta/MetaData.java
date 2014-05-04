/***********************************************************************************************************************
 * Copyright 2011 Kim Herzig, Sascha Just
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 **********************************************************************************************************************/

package org.mozkito.database.meta;

import java.net.URL;

import net.ownhero.dev.andama.exceptions.UnrecoverableError;

import org.mozkito.database.Entity;
import org.mozkito.database.Layout;
import org.mozkito.database.Layout.TableType;
import org.mozkito.database.constraints.column.NotNull;
import org.mozkito.database.constraints.column.PrimaryKey;
import org.mozkito.database.constraints.column.Unique;
import org.mozkito.database.constraints.table.Check;
import org.mozkito.database.exceptions.DatabaseException;
import org.mozkito.database.model.Column;
import org.mozkito.database.model.Table;
import org.mozkito.database.types.Type;

/**
 * The Class MetaData.
 * 
 * @author Sascha Just <sascha.just@mozkito.org>
 */
public class MetaData implements Entity {
	
	/** The Constant LAYOUT. */
	public static final Layout<MetaData> LAYOUT           = new Layout<>(MetaData.class);
	
	/** The Constant TABLE. */
	public static final Table            TABLE;
	
	static {
		try {
			// @formatter:off
			TABLE = new Table("metadata", 
			                  new Column("id", Type.getShort(), new PrimaryKey(), new NotNull()),
			                  new Column("module_name", Type.getVarChar(32), new NotNull(), new Unique()),
			                  new Column("update_check_url", Type.getText(), new NotNull()),
			                  new Column("module_version_major", Type.getShort(), new NotNull()), 
			                  new Column("module_version_minor", Type.getShort(), new NotNull()),
			                  new Column("module_version_revision", Type.getShort()),
			                  new Column("module_version_releaselevel", Type.getVarChar(Version.RELEASE_LEVEL.class), new NotNull()),
			                  new Column("module_version_release", Type.getShort()),
			                  new Column("model_version_major", Type.getShort(), new NotNull()), 
			                  new Column("model_version_minor", Type.getShort()),
			                  new Column("model_version_revision", Type.getShort()),
			                  new Column("model_version_releaselevel", Type.getVarChar(Version.RELEASE_LEVEL.class)),
			                  new Column("model_version_release", Type.getShort()));
			TABLE.setConstraints(
			                     new Check(TABLE.column("update_check_url") + " SIMILAR TO 'https?://update.mozkito.org/%'", TABLE.column("update_check_url")));
			
			LAYOUT.addTable(TABLE, TableType.MAIN);
			LAYOUT.makeImmutable();
			
			// @formatter:on
		} catch (final DatabaseException e) {
			throw new UnrecoverableError(e);
		}
	}
	
	/** The Constant serialVersionUID. */
	private static final long            serialVersionUID = 7678724869558142220L;
	
	/** The module. */
	private String                       module;
	
	/** The update check. */
	private URL                          updateCheck;
	
	/** The module version. */
	private Version                      moduleVersion;
	
	/** The model version. */
	private Version                      modelVersion;
	
	private short                        id;
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Entity#getClassName()
	 */
	@Override
	public String getClassName() {
		return getClass().getSimpleName();
	}
	
	/**
	 * {@inheritDoc}
	 * 
	 * @see org.mozkito.database.Artifact#getId()
	 */
	@Override
	public Object getId() {
		return this.id;
	}
	
	/**
	 * Gets the model version.
	 * 
	 * @return the modelVersion
	 */
	public Version getModelVersion() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.modelVersion;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the module.
	 * 
	 * @return the module
	 */
	public String getModule() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.module;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Gets the module version.
	 * 
	 * @return the moduleVersion
	 */
	public Version getModuleVersion() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.moduleVersion;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @return the updateCheck
	 */
	public URL getUpdateCheck() {
		PRECONDITIONS: {
			// none
		}
		
		try {
			return this.updateCheck;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the model version.
	 * 
	 * @param modelVersion
	 *            the modelVersion to set
	 */
	public void setModelVersion(final Version modelVersion) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.modelVersion = modelVersion;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the module.
	 * 
	 * @param module
	 *            the module to set
	 */
	public void setModule(final String module) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.module = module;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * Sets the module version.
	 * 
	 * @param moduleVersion
	 *            the moduleVersion to set
	 */
	public void setModuleVersion(final Version moduleVersion) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.moduleVersion = moduleVersion;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
	/**
	 * @param updateCheck
	 *            the updateCheck to set
	 */
	public void setUpdateCheck(final URL updateCheck) {
		PRECONDITIONS: {
			// none
		}
		
		try {
			this.updateCheck = updateCheck;
		} finally {
			POSTCONDITIONS: {
				// none
			}
		}
	}
	
}
