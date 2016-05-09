/*
 * Copyright (c) 2002-2015, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.easyrulesbot.modules.sitebuilder.service;

import fr.paris.lutece.plugins.easyrulesbot.modules.sitebuilder.business.Component;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.html.HtmlTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;


/**
 * PomBuilder
 */
public class PomBuilder
{
    private static final String TEMPLATE_POM_FILE = "skin/plugins/easyrulesbot/modules/sitebuilder/pom.xml";
    private static final String MARK_COMPONENTS_LIST = "components_list";
    private static Map<String, String> _mapMapping;

    /**
     * Set the mapping between bot data and artifact
     * @param mapMapping The mapping between bot data and artifact
     */
    public void setMapping( Map<String, String> mapMapping )
    {
        _mapMapping = mapMapping;
    }

    /**
     * Gets the component list
     * @param mapData the data provided by the bot
     * @return the component list
     */
    public static List<Component> getComponents( Map<String, String> mapData )
    {
        List<Component> list = new ArrayList<Component>(  );

        for ( String strKey : _mapMapping.keySet(  ) )
        {
            for( String strDataKey : mapData.keySet() )
            {
                String strData = strDataKey + ":" + mapData.get(strDataKey);
            
                if ( strData.equals( strKey ) )
                {
                    Component component = new Component(  );
                    String strArtifactId = _mapMapping.get( strKey );
                    component.setArtifactId( strArtifactId );
                    component.setVersion( VersionService.getVersion( strArtifactId ) );
                    list.add( component );
                }
            }
        }

        return list;
    }

    public String buildPom( Map<String, String> mapData )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        model.put( MARK_COMPONENTS_LIST, getComponents( mapData ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_POM_FILE, LocaleService.getDefault(  ), model );

        return template.getHtml(  );
    }
}
