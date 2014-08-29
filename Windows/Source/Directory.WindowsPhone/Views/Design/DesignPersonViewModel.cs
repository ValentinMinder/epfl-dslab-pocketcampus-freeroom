using System.Threading;
using PocketCampus.Common.Services.Design;
using PocketCampus.Directory.Models;
using PocketCampus.Directory.Services.Design;
using PocketCampus.Directory.ViewModels;
using ThinMvvm.WindowsPhone.Design;

namespace PocketCampus.Directory.Views.Design
{
    public sealed class DesignPersonViewModel : DesignViewModel<PersonViewModel, Person>
    {
#if DEBUG
        protected override PersonViewModel ViewModel
        {
            get
            {
                var person = new DesignDirectoryService().SearchAsync( null, CancellationToken.None ).Result.Results[0];
                return new PersonViewModel( new DesignBrowserService(), new DesignEmailService(), new DesignPhoneService(), new DesignContactsService(), person );
            }
        }
#endif
    }
}