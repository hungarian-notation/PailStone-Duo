package net.eonz.bukkit.psduo;

/*
 * This code is Copyright (C) 2011 Chris Bode, Some Rights Reserved.
 *
 * Copyright (C) 1999-2002 Technical Pursuit Inc., All Rights Reserved. Patent 
 * Pending, Technical Pursuit Inc.
 *
 * Unless explicitly acquired and licensed from Licensor under the Technical 
 * Pursuit License ("TPL") Version 1.0 or greater, the contents of this file are 
 * subject to the Reciprocal Public License ("RPL") Version 1.1, or subsequent 
 * versions as allowed by the RPL, and You may not copy or use this file in 
 * either source code or executable form, except in compliance with the terms and 
 * conditions of the RPL.
 *
 * You may obtain a copy of both the TPL and the RPL (the "Licenses") from 
 * Technical Pursuit Inc. at http://www.technicalpursuit.com.
 *
 * All software distributed under the Licenses is provided strictly on an "AS IS"
 * basis, WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESS OR IMPLIED, AND TECHNICAL
 * PURSUIT INC. HEREBY DISCLAIMS ALL SUCH WARRANTIES, INCLUDING WITHOUT 
 * LIMITATION, ANY WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
 * PURPOSE, QUIET ENJOYMENT, OR NON-INFRINGEMENT. See the Licenses for specific 
 * language governing rights and limitations under the Licenses. 
 */

import org.bukkit.World;
import org.bukkit.block.Block;

public class CuboidUtil {

	/**
	 * Utility for drawing cuboids.
	 * 
	 * @param materialID
	 * @param data
	 * @param world
	 * @param x1
	 * @param y1
	 * @param z1
	 * @param x2
	 * @param y2
	 * @param z2
	 */
	public static void drawCuboid(int materialID, byte data, World world, int x1, int y1, int z1, int x2, int y2, int z2, CuboidType type) {
		if (x1 > x2) {
			int x3 = x1;
			x1 = x2;
			x2 = x3;
		}
		if (y1 > y2) {
			int y3 = y1;
			y1 = y2;
			y2 = y3;
		}
		if (z1 > z2) {
			int z3 = z1;
			z1 = z2;
			z2 = z3;
		}

		switch (type) {
		case CUBOID:
			for (int x = x1; x <= x2; x++) {
				for (int y = y1; y <= y2; y++) {
					for (int z = z1; z <= z2; z++) {
						drawBlock(materialID, data, world, x, y, z);
					}
				}
			}
			break;
		case ELLIPSOID:
			
			/*
			 * Ellipsoid code taken with permission from Matvei Stefarov's fCraft.
			 * Copyright 2009, 2010, 2011 Matvei Stefarov <me@matvei.org>
			 */
			
			// find axis lengths
            double rx = (x2 - x1 + 1) / 2d;
            double rz = (z2 - z1 + 1) / 2d;
            double ry = (y2 - y1 + 1) / 2d;

            double rx2 = 1 / (rx * rx);
            double rz2 = 1 / (rz * rz);
            double ry2 = 1 / (ry * ry);

            // find center points
            double cx = (x2 + x1) / 2d;
            double cz = (z2 + z1) / 2d;
            double cy = (y2 + y1) / 2d;
            
            for( int z = z1; z <= z2; z++ ) {
                for( int y = y1; y < world.getMaxHeight() && y <= y2; y++ ) {
                    for( int x = x1; x <= x2; x++) {
                        double dx = (x - cx);
                        double dy = (y - cy);
                        double dz = (z - cz);

                        // test if it's inside ellipse
                        if( (dx * dx) * rx2 + (dz * dz) * rz2 + (dy * dy) * ry2 <= 1 ) {
                        	drawBlock(materialID, data, world, x, y, z);
                        }
                    }
                }
            }
			break;
		}
	}
	
	public static void drawBlock(int mat, byte dat, World world, int x, int y, int z) {
		Block b = world.getBlockAt(x, y, z);
		if (b.getTypeId() != mat || (b.getData() != dat && dat != -1)) {
			if (mat != -1) 
				b.setTypeId(mat);
			if (dat != -1)
				b.setData(dat);
		}
	}

	public static enum CuboidType {
		CUBOID('R'), ELLIPSOID('E');
		
		public final char ID;
		
		private CuboidType(char id) {
			this.ID = id;
		}
		
		public static CuboidType fromId(char id) {
			for (CuboidType type : CuboidType.values()) {
				if (type.ID == Character.toUpperCase(id)) {
					return type;
				}
			}
			
			return null;
		}
	}
}
