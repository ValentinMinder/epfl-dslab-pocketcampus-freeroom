// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Mvvm;

namespace PocketCampus.Common.Services
{
    /// <summary>
    /// Windows Phone specific extension of INavigationService.
    /// </summary>
    public interface IWindowsPhoneNavigationService : INavigationService
    {
        /// <summary>
        /// Binds the specified View URI to the specified ViewModel type.
        /// </summary>
        void Bind<TViewModel>( string viewUri );
    }
}