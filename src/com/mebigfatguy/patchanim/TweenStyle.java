/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008-2019 Dave Brosius
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package com.mebigfatguy.patchanim;

import java.util.Locale;
import java.util.ResourceBundle;

import com.mebigfatguy.patchanim.main.PatchAnimBundle;
import com.mebigfatguy.patchanim.surface.PatchGenerator;

/**
 * denotes the algorithm to use to tween two patches
 * <ul>
 * <li><b>Linear</b> Control points are tweened linearly from start to finish</li>
 * <li><b>Accelerating</b> Control points are tweened accelerating from start to finish</li>
 * <li><b>Decelerating</b> Control points are tweened decelerating from start to finish</li>
 * <li><b>EaseInEaseOut</b> Control points are tweened eased in from start, then eased out to finish</li>
 * <li><b>Accelerating</b> Control points are tweened accelerating in from start and then accelerated out to finish</li>
 * <li><b>Wave</b> Control points are tweened waved from start to finish</li>
 * </ul>
 */
public enum TweenStyle {
    Linear(0, 1), Accelerating(0, 0, 0, 1), Decelerating(0, 1, 1, 1), EaseInEaseOut(0, 0, 1, 1), AccelerateInAccelerateOut(0, 1, 0, 1), Wave(0, 2, -1, 1);

    private static final String TWEEN = "tweenstyle.";

    private double[] ys;

    private TweenStyle(double... ys) {
        this.ys = ys;
    }

    public double transform(double frac) {
        double[] coeffs = new double[ys.length];
        PatchGenerator.buildCoefficients(frac, coeffs);
        double out = 0.0;
        for (int i = 0; i < ys.length; i++) {
            out += coeffs[i] * ys[i];
        }
        return out;
    }

    /**
     * returns the localized value of the enum
     * 
     * @return the localized display value
     */
    @Override
    public String toString() {
        ResourceBundle rb = PatchAnimBundle.getBundle();
        return rb.getString(PatchAnimBundle.ROOT + TWEEN + name().toLowerCase(Locale.ENGLISH));
    }
}
