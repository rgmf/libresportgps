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

package es.rgmf.libresportgps.file.reader;

public interface IReader {
	public double getDistance();
	public Speed getSpeed();
	public Elevation getElevation();
	
	public class Speed {
		protected double max = 0d;
		protected double avg = 0d;
		public double getMax() {
			return max;
		}
		public void setMax(double max) {
			this.max = max;
		}
		public double getAvg() {
			return avg;
		}
		public void setAvg(double avg) {
			this.avg = avg;
		}
		
	}
	
	public class Elevation {
		protected double gain = 0d;
		protected double loss = 0d;
		protected double max = 0d;
		protected double min = 0d;
		public double getGain() {
			return gain;
		}
		public void setGain(double gain) {
			this.gain = gain;
		}
		public double getLoss() {
			return loss;
		}
		public void setLoss(double loss) {
			this.loss = loss;
		}
		public double getMax() {
			return max;
		}
		public void setMax(double max) {
			this.max = max;
		}
		public double getMin() {
			return min;
		}
		public void setMin(double min) {
			this.min = min;
		}
	}
}
