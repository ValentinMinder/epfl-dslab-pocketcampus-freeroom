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
        /// If no dialog is present on screen, navigates to a ViewModel of the specified type.
        /// Otherwise, does so after (and if) the current dialog is exited successfully.
        /// </summary>
        void NavigateTo<T>() where T : IViewModel<NoParameter>;

        /// <summary>
        /// If no dialog is present on screen, navigates to a ViewModel of the specified type.
        /// Otherwise, does so after (and if) the current dialog is exited successfully.
        /// </summary>
        void NavigateTo<TViewModel, TArg>( TArg arg ) where TViewModel : IViewModel<TArg>;

        /// <summary>
        /// Navigates to the specified dialog.
        /// </summary>
        void NavigateToDialog<T>() where T : IViewModel<NoParameter>;

        /// <summary>
        /// Navigates to the specified dialog.
        /// </summary>
        void NavigateToDialog<TViewModel, TArg>( TArg arg ) where TViewModel : IViewModel<TArg>;

        /// <summary>
        /// If no dialog was present, goes back to the previous ViewModel.
        /// Otherwise, goes forward. (this allows dialogs to be used as normal VMs)
        /// </summary>
        void NavigateBack();

        /// <summary>
        /// Pops the ViewModel back-stack, removing the current one so that going backwards will not go to it.
        /// </summary>
        void PopBackStack();
    }
}