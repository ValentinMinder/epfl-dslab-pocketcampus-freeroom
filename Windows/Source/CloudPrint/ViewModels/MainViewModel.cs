using System.Threading.Tasks;
using PocketCampus.Authentication;
using PocketCampus.CloudPrint.Models;
using PocketCampus.CloudPrint.Services;
using ThinMvvm;

namespace PocketCampus.CloudPrint.ViewModels
{
    public sealed class MainViewModel : ViewModel<PrintRequest>
    {
        private readonly INavigationService _navigationService;
        private readonly IPrintService _printService;
        private readonly IFileLoader _fileLoader;
        private readonly IFileUploader _fileUploader;
        private readonly PrintRequest _request;

        private PrintRequestStatus _status;

        public string FileName { get; private set; }
        public PrintSettings Settings { get; private set; }

        public PrintRequestStatus Status
        {
            get { return _status; }
            private set { SetProperty( ref _status, value ); }
        }


        public AsyncCommand PrintCommand
        {
            get { return this.GetAsyncCommand( PrintAsync ); }
        }


        public MainViewModel( INavigationService navigationService, IPrintService printService,
                              IFileLoader fileLoader, IFileUploader fileUploader,
                              PrintRequest request )
        {
            _navigationService = navigationService;
            _printService = printService;
            _fileLoader = fileLoader;
            _fileUploader = fileUploader;
            _request = request;

            FileName = request.DocumentName;
            Settings = request.Settings ?? PrintSettings.GetDefault();
        }

        private async Task PrintAsync()
        {
            Status = PrintRequestStatus.Printing;

            var serverRequest = new PrintDocumentRequest
            {
                ColorConfig = Settings.ColorConfig,
                CopiesConfig = Settings.CopiesConfig,
                Orientation = Settings.PageOrientation
            };

            if ( Settings.MultiPageConfig.PagesPerSheet != PagesPerSheet.One )
            {
                serverRequest.MultiPageConfig = Settings.MultiPageConfig;
            }

            if ( Settings.DoubleSidedConfig != DoubleSidedConfig.SingleSide )
            {
                serverRequest.DoubleSidedConfig = Settings.DoubleSidedConfig;
            }

            if ( !Settings.PrintAllPages )
            {
                serverRequest.Range = Settings.PageRange;
            }

            if ( _request.DocumentId == null )
            {
                try
                {
                    var stream = await _fileLoader.GetFileAsync( _request.FileUri );
                    var documentId = await _fileUploader.UploadFileAsync( _request.DocumentName, stream );
                    serverRequest.DocumentId = documentId;
                }
                catch ( AuthenticationRequiredException )
                {
                    ForceAuthentication();
                }
                catch
                {
                    Status = PrintRequestStatus.UploadError;
                    return;
                }
            }
            else
            {
                serverRequest.DocumentId = (long) _request.DocumentId;
            }

            try
            {
                var response = await _printService.PrintAsync( serverRequest );

                if ( response.Status == ResponseStatus.PrintError )
                {
                    Status = PrintRequestStatus.PrintingError;
                    return;
                }
                if ( response.Status == ResponseStatus.AuthenticationError )
                {
                    ForceAuthentication();
                }
            }
            catch
            {
                Status = PrintRequestStatus.PrintingError;
                return;
            }

            await _fileLoader.DeleteFileAsync( _request.FileUri );
            Status = PrintRequestStatus.Success;
        }

        private void ForceAuthentication()
        {
            var newRequest = new PrintRequest( _request, Settings );
            _navigationService.RemoveCurrentFromBackStack();
            Messenger.Send( new AuthenticationRequest( () => _navigationService.NavigateTo<MainViewModel, PrintRequest>( newRequest ) ) );
        }
    }
}