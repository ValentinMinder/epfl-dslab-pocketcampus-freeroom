// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// ViewModel with a constructor argument (apart from the potential dependencies) and navigation methods.
    /// </summary>
    public interface IViewModel<TArg>
    {
        /// <summary>
        /// Called when the user navigates to the ViewModel.
        /// </summary>
        void OnNavigatedTo();

        /// <summary>
        /// Called when the user navigates from the ViewModel.
        /// </summary>
        void OnNavigatedFrom();
    }
}