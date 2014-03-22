// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

namespace PocketCampus.Mvvm
{
    /// <summary>
    /// Navigation service between ViewModels.
    /// </summary>
    public interface INavigationService
    {
        /// <summary>
        /// Navigates to a ViewModel of the specified type.
        /// </summary>
        void NavigateTo<T>() where T : IViewModel<NoParameter>;

        /// <summary>
        /// Navigates to a ViewModel of the specified type, with the specified argument.
        /// </summary>
        void NavigateTo<TViewModel, TArg>( TArg arg ) where TViewModel : IViewModel<TArg>;

        /// <summary>
        /// Goes back to the previous ViewModel.
        /// </summary>
        void NavigateBack();

        /// <summary>
        /// Pops the ViewModel back-stack, removing the current one so that going backwards will not go to it.
        /// </summary>
        void PopBackStack();
    }
}