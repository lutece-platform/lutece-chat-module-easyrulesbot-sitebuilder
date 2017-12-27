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
package fr.paris.lutece.plugins.easyrulesbot.modules.sitebuilder.business;

import fr.paris.lutece.plugins.easyrulesbot.business.BotExecutor;
import fr.paris.lutece.plugins.easyrulesbot.modules.sitebuilder.Constants;
import fr.paris.lutece.plugins.easyrulesbot.modules.sitebuilder.service.PomBuilder;
import fr.paris.lutece.plugins.easyrulesbot.service.EasyRulesBot;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.mail.MailService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.web.LocalVariables;
import fr.paris.lutece.portal.web.l10n.LocaleService;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.mail.FileAttachment;
import java.util.ArrayList;
import java.util.List;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * SiteBuilder Bot
 */
public class SiteBuilderBot extends EasyRulesBot
{
    private static final String PROPERTY_LAST_MESSAGE = "module.easyrulesbot.sitebuilder.lastMessage";
    private static final String PROPERTY_MAIL_SENDER = "module.easyrulesbot.sitebuilder.mail.sender.name";
    private static final String PROPERTY_MAIL_SENDER_EMAIL = "module.easyrulesbot.sitebuilder.mail.sender.email";
    private static final String PROPERTY_MAIL_SUBJECT = "module.easyrulesbot.sitebuilder.mail.subject";
    private static final String PROPERTY_MAIL_MESSAGE = "module.easyrulesbot.sitebuilder.mail.message";
    private static final long serialVersionUID = 1L;
    private PomBuilder _pomBuilder;

    /**
     * Set the pom builder
     * 
     * @param pomBuilder
     *            The pom builder
     */
    public void setPomBuilder( PomBuilder pomBuilder )
    {
        _pomBuilder = pomBuilder;
    }

    /**
     * {@inheritDoc }
     */
    @Override
    public String processData( Map<String, String> mapData, Locale locale )
    {
        String strPom = _pomBuilder.buildPom( mapData );
        HttpServletRequest request = LocalVariables.getRequest();
        HttpSession session = request.getSession( true );
        session.setAttribute( Constants.SESSION_ATTRIBUTE_POM, strPom );

        String strEmail = mapData.get( BotExecutor.DATA_USER_EMAIL );
        if ( strEmail != null )
        {
            sendMail( strEmail, strPom, locale, mapData );
        }
        String strMessage = I18nService.getLocalizedString( PROPERTY_LAST_MESSAGE, locale );

        return strMessage;
    }

    /**
     * Send the pom.xml file by mail
     * 
     * @param strRecipient
     *            The recipient
     * @param strPom
     *            The POM content
     * @param locale
     *            The locale
     * @param mapData
     *            The data
     */
    private void sendMail( String strRecipient, String strPom, Locale locale, Map<String, String> mapData )
    {
        String strSender = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER, locale );
        String strSenderEmail = I18nService.getLocalizedString( PROPERTY_MAIL_SENDER_EMAIL, locale );
        String strSubject = I18nService.getLocalizedString( PROPERTY_MAIL_SUBJECT, locale );
        String strMessageTemplate = I18nService.getLocalizedString( PROPERTY_MAIL_MESSAGE, locale );
        HtmlTemplate template = AppTemplateService.getTemplateFromStringFtl( strMessageTemplate, LocaleService.getDefault( ), mapData );
        String strMessage = template.getHtml( );
        FileAttachment file = new FileAttachment( "pom.xml", strPom.getBytes( ), "text/plain" );
        List<FileAttachment> filesAttachement = new ArrayList<FileAttachment>( );
        filesAttachement.add( file );
        MailService.sendMailMultipartHtml( strRecipient, null, null, strSender, strSenderEmail, strSubject, strMessage, null, filesAttachement );
    }
}
