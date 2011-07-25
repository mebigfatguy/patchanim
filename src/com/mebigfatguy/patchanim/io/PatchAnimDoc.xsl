<!--
/*
 * patchanim - A bezier surface patch color blend animation builder
 * Copyright (C) 2008 Dave Brosius
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
 -->
<xsl:transform version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
							 xmlns:xalan="http://xml.apache.org/xalan"
							 xmlns:pae="xalan://com.mebigfatguy.patchanim.io.PatchAnimExtension"
							 extension-element-prefixes="pae">
	
	<xsl:output method="xml" 
              encoding="UTF-8"
              indent="yes" 
              xalan:indent-amount="4"/>
              
	<xsl:param name="doc"/>
	<xsl:variable name="ext" select="pae:new($doc)"/>
	
	<xsl:template match="/">
		<PatchAnimDoc>
			<xsl:attribute name="version">
				<xsl:value-of select="pae:getVersion($ext)"/>
			</xsl:attribute>
			<Settings>
				<xsl:attribute name="order">
					<xsl:value-of select="pae:getOrder($ext)"/>
				</xsl:attribute>
				<xsl:attribute name="alpha">
					<xsl:value-of select="pae:useAlpha($ext)"/>
				</xsl:attribute>
				<xsl:attribute name="width">
					<xsl:value-of select="pae:getWidth($ext)"/>
				</xsl:attribute>
				<xsl:attribute name="height">
					<xsl:value-of select="pae:getHeight($ext)"/>
				</xsl:attribute>
				<xsl:attribute name="animationType">
					<xsl:value-of select="pae:getAnimationType($ext)"/>
				</xsl:attribute>
				<xsl:attribute name="outOfBoundsColor">
					<xsl:value-of select="pae:getOutOfBoundsColor($ext)"/>
				</xsl:attribute>
				<xsl:attribute name="tweenCount">
					<xsl:value-of select="pae:getTweenCount($ext)"/>
				</xsl:attribute>
				<xsl:attribute name="tweenStyle">
					<xsl:value-of select="pae:getTweenStyle($ext)"/>
				</xsl:attribute>
			</Settings>
			<Patches>
				<xsl:for-each select="pae:getPatches($ext)">
					<xsl:variable name="patchIndex" select="."/>
					<CombinedPatch>
						<xsl:attribute name="name">
							<xsl:value-of select="pae:getPatchName($ext, $patchIndex)"/>
						</xsl:attribute>
						<Patch color="Red">
							<xsl:for-each select="pae:getCoordinates($ext)">
								<Coordinate>
									<xsl:attribute name="x">
										<xsl:value-of select="pae:getX($ext, 'Red', $patchIndex, .)"/>
									</xsl:attribute>
									<xsl:attribute name="y">
										<xsl:value-of select="pae:getY($ext, 'Red', $patchIndex, .)"/>
									</xsl:attribute>
									<xsl:attribute name="color">
										<xsl:value-of select="pae:getColor($ext, 'Red', $patchIndex, .)"/>
									</xsl:attribute>
								</Coordinate>
							</xsl:for-each>
						</Patch>
						<Patch color="Green">
							<xsl:for-each select="pae:getCoordinates($ext)">
								<Coordinate>
									<xsl:attribute name="x">
										<xsl:value-of select="pae:getX($ext, 'Green', $patchIndex, .)"/>
									</xsl:attribute>
									<xsl:attribute name="y">
										<xsl:value-of select="pae:getY($ext, 'Green', $patchIndex, .)"/>
									</xsl:attribute>
									<xsl:attribute name="color">
										<xsl:value-of select="pae:getColor($ext, 'Green', $patchIndex, .)"/>
									</xsl:attribute>
								</Coordinate>
							</xsl:for-each>
						</Patch>
						<Patch color="Blue">
							<xsl:for-each select="pae:getCoordinates($ext)">
								<Coordinate>
									<xsl:attribute name="x">
										<xsl:value-of select="pae:getX($ext, 'Blue', $patchIndex, .)"/>
									</xsl:attribute>
									<xsl:attribute name="y">
										<xsl:value-of select="pae:getY($ext, 'Blue', $patchIndex, .)"/>
									</xsl:attribute>
									<xsl:attribute name="color">
										<xsl:value-of select="pae:getColor($ext, 'Blue', $patchIndex, .)"/>
									</xsl:attribute>
								</Coordinate>
							</xsl:for-each>
						</Patch>
						<xsl:if test="pae:useAlpha($ext)='true'">
							<Patch color="Alpha">
								<xsl:for-each select="pae:getCoordinates($ext)">
									<Coordinate>
										<xsl:attribute name="x">
											<xsl:value-of select="pae:getX($ext, 'Alpha', $patchIndex, .)"/>
										</xsl:attribute>
										<xsl:attribute name="y">
											<xsl:value-of select="pae:getY($ext, 'Alpha', $patchIndex, .)"/>
										</xsl:attribute>
										<xsl:attribute name="color">
											<xsl:value-of select="pae:getColor($ext, 'Alpha', $patchIndex, .)"/>
										</xsl:attribute>
									</Coordinate>
								</xsl:for-each>
							</Patch>
						</xsl:if>
					</CombinedPatch>
				</xsl:for-each>
			</Patches>
		</PatchAnimDoc>
	</xsl:template>

</xsl:transform>