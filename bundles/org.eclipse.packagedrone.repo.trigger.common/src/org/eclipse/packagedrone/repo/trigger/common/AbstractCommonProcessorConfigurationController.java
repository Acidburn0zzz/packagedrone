/*******************************************************************************
 * Copyright (c) 2016 IBH SYSTEMS GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBH SYSTEMS GmbH - initial API and implementation
 *******************************************************************************/
package org.eclipse.packagedrone.repo.trigger.common;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

import org.eclipse.packagedrone.repo.trigger.TriggerHandle;
import org.eclipse.packagedrone.repo.trigger.TriggerProcessor;
import org.eclipse.packagedrone.repo.trigger.TriggeredChannel;
import org.eclipse.packagedrone.repo.web.utils.ChannelServiceController;
import org.eclipse.packagedrone.web.ModelAndView;
import org.eclipse.packagedrone.web.RequestMapping;
import org.eclipse.packagedrone.web.RequestMethod;
import org.eclipse.packagedrone.web.common.CommonController;
import org.eclipse.packagedrone.web.controller.binding.BindingResult;
import org.eclipse.packagedrone.web.controller.binding.RequestParameter;

public abstract class AbstractCommonProcessorConfigurationController<T> extends ChannelServiceController
{
    protected abstract ModelAndView handleEditUpdate ( final TriggeredChannel channel, final TriggerHandle triggerHandle, final TriggerProcessor triggerProcessor, final T command, final BindingResult result );

    protected abstract ModelAndView handleCreateUpdate ( final TriggeredChannel channel, final TriggerHandle triggerHandle, final T command, final BindingResult result );

    protected abstract ModelAndView handleEdit ( final TriggeredChannel channel, final TriggerHandle trigger, final TriggerProcessor triggerProcessor );

    protected abstract ModelAndView handleCreate ( final TriggeredChannel channel, final TriggerHandle trigger );

    @RequestMapping ( method = RequestMethod.GET )
    public ModelAndView configure ( @RequestParameter ( "channelId" ) final String channelId, @RequestParameter ( "triggerId" ) final String triggerId, @RequestParameter (
            value = "processorId", required = false ) final String processorId)
    {
        return withChannel ( channelId, TriggeredChannel.class, channel -> {

            final Optional<TriggerHandle> trigger = channel.getTrigger ( triggerId );

            if ( !trigger.isPresent () )
            {
                return CommonController.createNotFound ( "trigger", triggerId );
            }

            if ( processorId == null || processorId.isEmpty () )
            {
                // handle create
                return handleCreate ( channel, trigger.get () );
            }
            else
            {
                // handle edit
                final Optional<TriggerProcessor> processor = trigger.get ().getProcessor ( processorId );
                if ( !processor.isPresent () )
                {
                    return CommonController.createNotFound ( "processor", triggerId );
                }
                return handleEdit ( channel, trigger.get (), processor.get () );
            }
        } );
    }

    public ModelAndView processUpdate ( final String channelId, final String triggerId, final String processorId, final T command, final BindingResult result )
    {
        return withChannel ( channelId, TriggeredChannel.class, channel -> {

            final Optional<TriggerHandle> trigger = channel.getTrigger ( triggerId );

            if ( !trigger.isPresent () )
            {
                return CommonController.createNotFound ( "trigger", triggerId );
            }

            if ( processorId == null || processorId.isEmpty () )
            {
                // handle create
                return handleCreateUpdate ( channel, trigger.get (), command, result );
            }
            else
            {
                // handle edit
                final Optional<TriggerProcessor> processor = trigger.get ().getProcessor ( processorId );
                if ( !processor.isPresent () )
                {
                    return CommonController.createNotFound ( "processor", processorId );
                }
                return handleEditUpdate ( channel, trigger.get (), processor.get (), command, result );
            }
        } );
    }

    protected Map<String, Object> makeModel ( final TriggeredChannel channel, final TriggerHandle trigger, final TriggerProcessor processor, final boolean edit, final Supplier<T> configuration )
    {
        final Map<String, Object> result = new HashMap<> ();

        result.put ( "buttonLabel", edit ? "Update" : "Create" );
        result.put ( "command", configuration.get () );
        result.put ( "channelId", channel.getId ().getId () );
        result.put ( "triggerId", trigger.getId () );

        if ( processor != null )
        {
            result.put ( "processorId", processor.getId () );
        }

        fillModel ( result );

        return result;
    }

    protected void fillModel ( final Map<String, Object> model )
    {
    }
}
