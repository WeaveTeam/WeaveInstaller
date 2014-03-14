/*
    Weave (Web-based Analysis and Visualization Environment)
    Copyright (C) 2008-2014 University of Massachusetts Lowell

    This file is a part of Weave.

    Weave is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License, Version 3,
    as published by the Free Software Foundation.

    Weave is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Weave.  If not, see <http://www.gnu.org/licenses/>.
*/

package weave.managers;

import java.net.URL;

public class IconManager
{
	public static URL ICON_TRAY_ONLINE = IconManager.class.getResource("/resources/logo_greed.png");
	public static URL ICON_TRAY_OFFLINE = IconManager.class.getResource("/resources/logo_orange.png");
	public static URL ICON_TRAY_ERROR = IconManager.class.getResource("/resources/logo_red.png");
	
	public static URL ICON_OIC_LOGO = IconManager.class.getResource("/resources/oic4.png");
}