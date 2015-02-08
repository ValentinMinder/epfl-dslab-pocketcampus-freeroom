// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using PocketCampus.CloudPrint.Models;
using ThinMvvm;

namespace PocketCampus.CloudPrint
{
    public sealed class PrintSettings : ObservableObject
    {
        private ColorConfig _colorConfig;
        private CopiesConfig _copiesConfig;
        private DoubleSidedConfig _doubleSidedConfig;
        private MultiPageConfig _multiPageConfig;
        private PageOrientation _pageOrientation;
        private bool _printAllPages;
        private PageRange _pageRange;

        public ColorConfig ColorConfig
        {
            get { return _colorConfig; }
            set { SetProperty( ref _colorConfig, value ); }
        }

        public CopiesConfig CopiesConfig
        {
            get { return _copiesConfig; }
            set { SetProperty( ref _copiesConfig, value ); }
        }

        public DoubleSidedConfig DoubleSidedConfig
        {
            get { return _doubleSidedConfig; }
            set { SetProperty( ref _doubleSidedConfig, value ); }
        }

        public MultiPageConfig MultiPageConfig
        {
            get { return _multiPageConfig; }
            set { SetProperty( ref _multiPageConfig, value ); }
        }

        public PageOrientation PageOrientation
        {
            get { return _pageOrientation; }
            set { SetProperty( ref _pageOrientation, value ); }
        }

        public bool PrintAllPages
        {
            get { return _printAllPages; }
            set { SetProperty( ref _printAllPages, value ); }
        }

        public PageRange PageRange
        {
            get { return _pageRange; }
            set { SetProperty( ref _pageRange, value ); }
        }

        public static PrintSettings GetDefault()
        {
            return new PrintSettings
            {
                ColorConfig = ColorConfig.BlackAndWhite,
                CopiesConfig = new CopiesConfig
                {
                    Collate = true,
                    Count = 1
                },
                DoubleSidedConfig = DoubleSidedConfig.LongEdge,
                MultiPageConfig = new MultiPageConfig
                {
                    Layout = MultiPageLayout.LeftToRightTopToBottom,
                    PagesPerSheet = PagesPerSheet.One
                },
                PageOrientation = PageOrientation.Portrait,
                PageRange = new PageRange
                {
                    From = 1,
                    To = 1
                },
                PrintAllPages = true
            };
        }
    }
}