// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for AddStationViewModel

using ThinMvvm;
namespace PocketCampus.Transport.ViewModels.Design
{
    public sealed class DesignAddStationViewModel
    {
#if DEBUG
        public DataStatus DataStatus { get { return DataStatus.DataLoaded; } }
#endif
    }
}