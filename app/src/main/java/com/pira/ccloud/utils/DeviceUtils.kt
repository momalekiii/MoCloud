package com.pira.ccloud.utils

import android.content.res.Configuration
import android.content.res.Resources

object DeviceUtils {
    /**
     * Checks if the device is a tablet based on screen size and density.
     * A device is considered a tablet if its smallest width is 600dp or more.
     */
    fun isTablet(resources: Resources): Boolean {
        val configuration = resources.configuration
        val smallestScreenWidthDp = configuration.smallestScreenWidthDp
        return smallestScreenWidthDp >= 600
    }
    
    /**
     * Returns the number of columns for grid layouts based on device type.
     * Tablets get 3 columns, phones get 2 columns.
     */
    fun getGridColumns(resources: Resources): Int {
        return if (isTablet(resources)) 4 else 2
    }
}