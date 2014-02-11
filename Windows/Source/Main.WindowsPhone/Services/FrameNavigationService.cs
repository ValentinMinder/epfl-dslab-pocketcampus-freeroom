// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using System;
using System.Collections.Generic;
using System.Windows;
using System.Windows.Navigation;
using Microsoft.Phone.Controls;
using PocketCampus.Common.Services;
using PocketCampus.Mvvm;
using PocketCampus.Mvvm.Logging;

namespace PocketCampus.Main.Services
{
    /// <summary>
    /// An INavigationService that uses a PhoneApplicationFrame to navigate.
    /// </summary>
    public sealed class FrameNavigationService : IWindowsPhoneNavigationService
    {
        private const string ListPickerPageToken = "ListPickerPage.xaml";

        private readonly NavigationLogger _logger;
        private readonly Dictionary<Type, Uri> _views;
        // HACK: IViewModel can't be covariant to be used with value types
        //       and having a non-generic IViewModel that shouldn't be implemented is a terrible idea
        //       so we use dynamic to call OnNavigatedTo/From
        private readonly Stack<dynamic> _backStack;

        private bool _isInDialog;
        private bool _ignoreNext;
        private object _afterDialog;
        private bool _removeCurrentFromBackstack;

        private PhoneApplicationFrame _rootFrame
        {
            get { return (PhoneApplicationFrame) Application.Current.RootVisual; }
        }


        /// <summary>
        /// Creates a new FrameNavigationService.
        /// </summary>
        public FrameNavigationService( NavigationLogger logger )
        {
            _logger = logger;
            _views = new Dictionary<Type, Uri>();
            _backStack = new Stack<dynamic>();

            _rootFrame.Navigated += Frame_Navigated;
        }


        /// <summary>
        /// Navigates to the specified ViewModel.
        /// </summary>
        private void NavigateToPrivate( object viewModel )
        {
            var viewModelType = viewModel.GetType();
            _logger.LogNavigation( viewModel, true );
            _backStack.Push( viewModel );
            _rootFrame.Navigate( _views[viewModelType] );
        }

        /// <summary>
        /// Navigates to the specified ViewModel, as a dialog.
        /// </summary>
        private void NavigateToDialogPrivate( object viewModel )
        {
            if ( _isInDialog )
            {
                throw new InvalidOperationException( "Cannot navigate to a dialog while one is in progress." );
            }

            _isInDialog = true;
            NavigateToPrivate( viewModel );
        }

        /// <summary>
        /// Occurs when the frame has navigated, either because the user requested it or because the program did.
        /// </summary>
        private void Frame_Navigated( object sender, NavigationEventArgs e )
        {
            if ( _ignoreNext )
            {
                _ignoreNext = false;
                return;
            }

            // HACK: The ListPicker uses a page when it has >5 elements (not a popup), which has to be ignored
            if ( e.Uri.ToString().Contains( ListPickerPageToken ) )
            {
                _ignoreNext = true;
                return;
            }

            var page = (PhoneApplicationPage) _rootFrame.Content;

            // need to check IsNavigationInitiator to avoid doing stuff when the user
            // long-presses the Back button to multitask
            if ( e.NavigationMode == NavigationMode.Back && e.IsNavigationInitiator )
            {
                _isInDialog = false;

                if ( _backStack.Count > 0 )
                {
                    var currentTop = _backStack.Pop();
                    currentTop.OnNavigatedFrom();
                    DisposeIfNeeded( currentTop );
                }
                if ( _backStack.Count > 0 )
                {
                    var currentViewModel = _backStack.Peek();
                    currentViewModel.OnNavigatedTo();
                    page.DataContext = currentViewModel;
                    _logger.LogNavigation( currentViewModel, false );
                }
            }
            else if ( e.NavigationMode == NavigationMode.Forward || e.NavigationMode == NavigationMode.New )
            {
                if ( _removeCurrentFromBackstack )
                {
                    _rootFrame.RemoveBackEntry();

                    var newTop = _backStack.Pop();
                    var currentTop = _backStack.Pop();
                    _backStack.Push( newTop );

                    DisposeIfNeeded( currentTop );

                    _removeCurrentFromBackstack = false;
                }

                if ( _backStack.Count > 0 )
                {
                    var currentViewModel = _backStack.Peek();
                    currentViewModel.OnNavigatedTo();
                    page.DataContext = currentViewModel;
                    _logger.LogNavigation( currentViewModel, false );
                }
            }
        }

        /// <summary>
        /// Disposes of the specified object, if it is an IDisposable.
        /// </summary>
        private static void DisposeIfNeeded( object obj )
        {
            var disposable = obj as IDisposable;
            if ( disposable != null )
            {
                disposable.Dispose();
            }
        }

        #region INavigationService implementation
        /// <summary>
        /// If no dialog is present on screen, navigates to a ViewModel of the specified type.
        /// Otherwise, does so after (and if) the current dialog is exited successfully.
        /// </summary>
        public void NavigateTo<T>()
            where T : IViewModel<NoParameter>
        {
            var vmType = typeof( T );
            var vm = Container.Get( vmType, null );

            if ( _isInDialog )
            {
                _afterDialog = vm;
            }
            else
            {
                NavigateToPrivate( vm );
            }
        }

        /// <summary>
        /// If no dialog is present on screen, navigates to a ViewModel of the specified type.
        /// Otherwise, does so after (and if) the current dialog is exited successfully.
        /// </summary>
        public void NavigateTo<TViewModel, TArg>( TArg arg )
            where TViewModel : IViewModel<TArg>
        {
            var vmType = typeof( TViewModel );
            var vm = Container.Get( vmType, arg );

            if ( _isInDialog )
            {
                _afterDialog = vm;
            }
            else
            {
                NavigateToPrivate( vm );
            }
        }

        /// <summary>
        /// Navigates to the specified dialog.
        /// </summary>
        public void NavigateToDialog<T>()
            where T : IViewModel<NoParameter>
        {
            var vmType = typeof( T );
            var vm = Container.Get( vmType, null );

            NavigateToDialogPrivate( vm );
        }

        /// <summary>
        /// Navigates to the specified dialog.
        /// </summary>
        public void NavigateToDialog<TViewModel, TArg>( TArg arg )
            where TViewModel : IViewModel<TArg>
        {
            var vmType = typeof( TViewModel );
            var vm = Container.Get( vmType, arg );

            NavigateToDialogPrivate( vm );
        }

        /// <summary>
        /// If no dialog was present, goes back to the previous ViewModel.
        /// Otherwise, goes forward. (this allows dialogs to be used as normal VMs)
        /// </summary>
        public void NavigateBack()
        {
            if ( _isInDialog )
            {
                _isInDialog = false;
                _removeCurrentFromBackstack = true;
                NavigateToPrivate( _afterDialog );
                _afterDialog = null;
            }
            else
            {
                if ( _backStack.Count > 0 )
                {
                    _rootFrame.GoBack();
                }
                else
                {
                    Application.Current.Terminate();
                }
            }
        }

        /// <summary>
        /// Pops the back-stack used by this navigation service.
        /// Used to ignore the current ViewModel when going back.
        /// </summary>
        public void PopBackStack()
        {
            _removeCurrentFromBackstack = true;
        }
        #endregion

        #region IWindowsPhoneNavigationService
        /// <summary>
        /// Adds a ViewModel to View URI link.
        /// </summary>
        /// <typeparam name="TViewModel">The ViewModel's type.</typeparam>
        /// <param name="viewUri">The View URI. Needs to be relative to the app root (e.g. /MyApp;Component/[...]).</param>
        public void Bind<TViewModel>( string viewUri )
        {
            _views.Add( typeof( TViewModel ), new Uri( viewUri, UriKind.Relative ) );
        }
        #endregion
    }
}