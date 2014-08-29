// Copyright (c) PocketCampus.Org 2014
// See LICENSE file for more details
// File author: Solal Pirelli

// Design data for CourseViewModel

#if DEBUG
using System.Linq;
using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.Moodle.Services.Design;
#endif
using PocketCampus.Moodle.Models;
using PocketCampus.Moodle.ViewModels;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Moodle.Views.Design
{
    public sealed class DesignCourseViewModel : DesignViewModel<CourseViewModel, Course>
    {
#if DEBUG
        protected override CourseViewModel ViewModel
        {
            get
            {
                var moodleService = new DesignMoodleService();
                var course = moodleService.GetCoursesAsync( null, CancellationToken.None ).Result.Courses.Last();
                return new CourseViewModel( new DesignDataCache(), new DesignSecureRequestHandler(), moodleService,
                                            new DesignMoodleDownloader(), new DesignFileStorage(), new DesignBrowserService(),
                                            course );
            }
        }
#endif
    }
}