// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for CourseViewModel

using System;
using System.Linq;
using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.Moodle.Services.Design;
using PocketCampus.Moodle.ViewModels;
using ThinMvvm;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Moodle.Views.Design
{
    public sealed class DesignCourseViewModel : DesignViewModel<CourseViewModel>
    {
#if DEBUG
        private CourseViewModel _viewModel;
        protected override CourseViewModel ViewModel
        {
            get
            {
                if ( _viewModel == null )
                {
                    var moodleService = new DesignMoodleService();
                    var course = moodleService.GetCoursesAsync( null, CancellationToken.None ).Result.Courses.Last();
                    _viewModel = new CourseViewModel( new DesignDataCache(), new DesignSecureRequestHandler(), moodleService, new DesignMoodleDownloader(), new DesignFileStorage(), new DesignBrowserService(), course );
                    _viewModel.OnNavigatedToAsync().Wait();
                }
                return _viewModel;
            }
        }

        public CourseViewModel ViewModelPublic { get { return ViewModel; } }

#warning this should not exist (bis)
        private sealed class DesignDataCache : IDataCache
        {
            public void Set( Type owner, long id, DateTime expirationDate, object value ) { }

            public bool TryGet<T>( Type owner, long id, out T value )
            {
                value = default( T );
                return false;
            }
        }
#endif
    }
}