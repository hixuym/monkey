/*
 * Copyright (C) 2016. the original author or authors.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.sunflower.orm;

import io.ebean.BeanState;
import io.ebean.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.Version;
import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author michael
 * created on 16/5/28
 */
@MappedSuperclass
public class AbstractEntity extends Model implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private Long id;

    @Version
    private Long version;

    @CreatedTimestamp
    private Timestamp whenCreated;

    @UpdatedTimestamp
    private Timestamp whenUpdated;

    /**
     * @return the id
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * @return the version
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version the version to set
     */
    public void setVersion(Long version) {
        this.version = version;
    }

    /**
     * @return the whenCreated
     */
    public Timestamp getWhenCreated() {
        return whenCreated;
    }

    /**
     * @param whenCreated the whenCreated to set
     */
    public void setWhenCreated(Timestamp whenCreated) {
        this.whenCreated = whenCreated;
    }

    /**
     * @return the whenUpdated
     */
    public Timestamp getWhenUpdated() {
        return whenUpdated;
    }

    /**
     * @param whenUpdated the whenUpdated to set
     */
    public void setWhenUpdated(Timestamp whenUpdated) {
        this.whenUpdated = whenUpdated;
    }

    /**
     * 未持久化的临时对象
     */
    public boolean isNew() {
        return db().getBeanState(this).isNew();
    }

    public boolean isNewOrDirty() {
        return db().getBeanState(this).isNewOrDirty();
    }

    public BeanState state() {
        return db().getBeanState(this);
    }

}
