/**
 * Copyright (C) 2014 Román Ginés Martínez Ferrández <rgmf@riseup.net>
 *
 * This program (LibreSportGPS) is free software: you can 
 * redistribute it and/or modify it under the terms of the GNU General 
 * Public License as published by the Free Software Foundation, either
 * version 3 of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package es.rgmf.libresportgps.db.orm;

import java.io.Serializable;

/**
 * Sport Object Relational Mapping.
 * 
 * @author Román Ginés Martínez Ferrández <rgmf@riseup.net>
 */
public class Sport implements Serializable {
	public static final int CANICROSS = 1;
	public static final int CYCLING = 2;
	public static final int MTB = 3;
	public static final int RUNNING = 4;
	public static final int TRAIL = 5;
	public static final int TREKKING = 6;
	
	private Long id;
	private String name;
	private String description;
	private String logo;
    public Sport() {}
    public Sport(Long id, String name, String description, String logo) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.logo = logo;
    }
	public Long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	
}
