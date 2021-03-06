/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.unistpixel.unistpixeldungeon.levels;

import com.unistpixel.unistpixeldungeon.UNISTPixelDungeon;
import com.unistpixel.unistpixeldungeon.levels.painters.AltarPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.ArmoryPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.BlacksmithPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.BossExitPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.CryptPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.EntrancePainter;
import com.unistpixel.unistpixeldungeon.levels.painters.ExitPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.GardenPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.LaboratoryPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.LibraryPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.MagicWellPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.MassGravePainter;
import com.unistpixel.unistpixeldungeon.levels.painters.Painter;
import com.unistpixel.unistpixeldungeon.levels.painters.PassagePainter;
import com.unistpixel.unistpixeldungeon.levels.painters.PitPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.PoolPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.RatKingPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.RitualSitePainter;
import com.unistpixel.unistpixeldungeon.levels.painters.RotGardenPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.ShopPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.StandardPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.StatuePainter;
import com.unistpixel.unistpixeldungeon.levels.painters.StoragePainter;
import com.unistpixel.unistpixeldungeon.levels.painters.TrapsPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.TreasuryPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.TunnelPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.VaultPainter;
import com.unistpixel.unistpixeldungeon.levels.painters.WeakFloorPainter;
import com.watabou.utils.Bundlable;
import com.watabou.utils.Bundle;
import com.watabou.utils.Graph;
import com.watabou.utils.Point;
import com.watabou.utils.Random;
import com.watabou.utils.Rect;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;

public class Room extends Rect implements Graph.Node, Bundlable {
	
	public ArrayList<Room> neigbours = new ArrayList<Room>();
	public LinkedHashMap<Room, Door> connected = new LinkedHashMap<Room, Door>();
	
	public int distance;
	public int price = 1;
	
	public static enum Type {
		NULL( null ),
		STANDARD	( StandardPainter.class ),
		ENTRANCE	( EntrancePainter.class ),
		EXIT		( ExitPainter.class ),
		BOSS_EXIT	( BossExitPainter.class ),
		TUNNEL		( TunnelPainter.class ),
		PASSAGE		( PassagePainter.class ),
		SHOP		( ShopPainter.class ),
		BLACKSMITH	( BlacksmithPainter.class ),
		TREASURY	( TreasuryPainter.class ),
		ARMORY		( ArmoryPainter.class ),
		LIBRARY		( LibraryPainter.class ),
		LABORATORY	( LaboratoryPainter.class ),
		VAULT		( VaultPainter.class ),
		TRAPS		( TrapsPainter.class ),
		STORAGE		( StoragePainter.class ),
		MAGIC_WELL	( MagicWellPainter.class ),
		GARDEN		( GardenPainter.class ),
		CRYPT		( CryptPainter.class ),
		STATUE		( StatuePainter.class ),
		POOL		( PoolPainter.class ),
		RAT_KING	( RatKingPainter.class ),
		WEAK_FLOOR	( WeakFloorPainter.class ),
		PIT			( PitPainter.class ),
		ALTAR		( AltarPainter.class ),

		//prison quests
		MASS_GRAVE  ( MassGravePainter.class ),
		ROT_GARDEN  ( RotGardenPainter.class ),
		RITUAL_SITE ( RitualSitePainter.class );
		
		private Method paint;
		
		Type( Class<? extends Painter> painter ) {
			if (painter == null)
				paint = null;
			else
				try {
					paint = painter.getMethod( "paint", Level.class, Room.class );
				} catch (Exception e) {
					UNISTPixelDungeon.reportException(e);
					paint = null;
				}
		}
		
		public void paint( Level level, Room room ) {
			try {
				paint.invoke( null, level, room );
			} catch (Exception e) {
				UNISTPixelDungeon.reportException(e);
			}
		}
	};

	private static final ArrayList<Type> ALL_SPEC = new ArrayList<Type>( Arrays.asList(
		Type.WEAK_FLOOR, Type.MAGIC_WELL, Type.CRYPT, Type.POOL, Type.GARDEN, Type.LIBRARY, Type.ARMORY,
		Type.TREASURY, Type.TRAPS, Type.STORAGE, Type.STATUE, Type.LABORATORY, Type.VAULT
	) );
	
	public static ArrayList<Type> SPECIALS = new ArrayList<Type>( Arrays.asList(
		Type.WEAK_FLOOR, Type.MAGIC_WELL, Type.CRYPT, Type.POOL, Type.GARDEN, Type.LIBRARY, Type.ARMORY,
		Type.TREASURY, Type.TRAPS, Type.STORAGE, Type.STATUE, Type.LABORATORY, Type.VAULT
	) );
	
	public Type type = Type.NULL;
	
	public Point random() {
		return random( 0 );
	}
	
	public Point random( int m ) {
		return new Point( Random.Int( left + 1 + m, right - m ),
				Random.Int( top + 1 + m, bottom - m ));
	}
	
	public void addNeigbour( Room other ) {
		
		Rect i = intersect( other );
		if ((i.width() == 0 && i.height() >= 3) ||
			(i.height() == 0 && i.width() >= 3)) {
			neigbours.add( other );
			other.neigbours.add( this );
		}
		
	}
	
	public void connect( Room room ) {
		if (!connected.containsKey( room )) {
			connected.put( room, null );
			room.connected.put( this, null );
		}
	}
	
	public Door entrance() {
		return connected.values().iterator().next();
	}
	
	public boolean inside( Point p ) {
		return p.x > left && p.y > top && p.x < right && p.y < bottom;
	}
	
	public Point center() {
		return new Point(
			(left + right) / 2 + (((right - left) & 1) == 1 ? Random.Int( 2 ) : 0),
			(top + bottom) / 2 + (((bottom - top) & 1) == 1 ? Random.Int( 2 ) : 0) );
	}
	
	// **** Graph.Node interface ****

	@Override
	public int distance() {
		return distance;
	}

	@Override
	public void distance( int value ) {
		distance = value;
	}
	
	@Override
	public int price() {
		return price;
	}

	@Override
	public void price( int value ) {
		price = value;
	}

	@Override
	public Collection<Room> edges() {
		return neigbours;
	}
	
	@Override
	public void storeInBundle( Bundle bundle ) {
		bundle.put( "left", left );
		bundle.put( "top", top );
		bundle.put( "right", right );
		bundle.put( "bottom", bottom );
		bundle.put( "type", type.toString() );
	}
	
	@Override
	public void restoreFromBundle( Bundle bundle ) {
		left = bundle.getInt( "left" );
		top = bundle.getInt( "top" );
		right = bundle.getInt( "right" );
		bottom = bundle.getInt( "bottom" );
		type = Type.valueOf( bundle.getString( "type" ) );
	}
	
	public static void shuffleTypes() {
		SPECIALS = (ArrayList<Type>)ALL_SPEC.clone();
		int size = SPECIALS.size();
		for (int i=0; i < size - 1; i++) {
			int j = Random.Int( i, size );
			if (j != i) {
				Type t = SPECIALS.get( i );
				SPECIALS.set( i, SPECIALS.get( j ) );
				SPECIALS.set( j, t );
			}
		}
	}
	
	public static void useType( Type type ) {
		if (SPECIALS.remove( type )) {
			SPECIALS.add( type );
		}
	}
	
	private static final String ROOMS	= "rooms";
	
	public static void restoreRoomsFromBundle( Bundle bundle ) {
		if (bundle.contains( ROOMS )) {
			SPECIALS.clear();
			for (String type : bundle.getStringArray( ROOMS )) {
				SPECIALS.add( Type.valueOf( type ));
			}
		} else {
			shuffleTypes();
		}
	}
	
	public static void storeRoomsInBundle( Bundle bundle ) {
		String[] array = new String[SPECIALS.size()];
		for (int i=0; i < array.length; i++) {
			array[i] = SPECIALS.get( i ).toString();
		}
		bundle.put( ROOMS, array );
	}
	
	public static class Door extends Point {
		
		public static enum Type {
			EMPTY, TUNNEL, REGULAR, UNLOCKED, HIDDEN, BARRICADE, LOCKED
		}
		public Type type = Type.EMPTY;
		
		public Door( int x, int y ) {
			super( x, y );
		}
		
		public void set( Type type ) {
			if (type.compareTo( this.type ) > 0) {
				this.type = type;
			}
		}
	}
}