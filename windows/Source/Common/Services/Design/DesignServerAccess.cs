// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

// Design implementation for IServerAccess

#if DEBUG
using System;
using System.Threading.Tasks;
using ThriftSharp;

namespace PocketCampus.Common.Services.Design
{
    public sealed class DesignServerAccess : IServerAccess
    {
        public Task<ServerConfiguration> LoadConfigurationAsync()
        {
            return Task.FromResult
            (
                new ServerConfiguration
                {
                    EnabledPlugins = new[] { "Authentication", "Camipro", "Directory", "Events", "Food", "IsAcademia", "Map", "Moodle", "News", "Satellite", "Transport" }
                }
            );
        }

        public ThriftCommunication CreateCommunication( string pluginName )
        {
            throw new NotSupportedException();
        }
    }
}
#endif