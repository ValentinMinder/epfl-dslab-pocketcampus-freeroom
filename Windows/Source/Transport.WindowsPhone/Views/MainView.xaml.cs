// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.Common.Controls;
using PocketCampus.Transport.Models;
using PocketCampus.Transport.ViewModels;

namespace PocketCampus.Transport.Views
{
    public partial class MainView : BasePage
    {
        public MainView()
        {
            InitializeComponent();

            Loaded += ( _, __ ) =>
            {
                var vm = (MainViewModel) DataContext;
                vm.PropertyChanged += ( ___, e ) =>
                {
                    if ( e.PropertyName == "SelectedStation" || ( StationsPicker.SelectedItem == null && e.PropertyName == "IsLoading" && !vm.IsLoading ) )
                    {
                        if ( vm.SelectedStation != StationsPicker.SelectedItem )
                        {
                            StationsPicker.SelectedItem = vm.SelectedStation;
                        }
                    }
                };

                StationsPicker.SelectionChanged += ( ___, e ) =>
                {
                    if ( e.RemovedItems.Count > 0 )
                    {
                        vm.SelectedStation = (Station) e.AddedItems[0];
                    }
                };
            };
        }
    }
}