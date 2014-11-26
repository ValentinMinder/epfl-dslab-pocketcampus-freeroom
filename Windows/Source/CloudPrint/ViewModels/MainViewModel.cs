using System.Threading.Tasks;
using PocketCampus.CloudPrint.Models;
using PocketCampus.CloudPrint.Services;
using ThinMvvm;

namespace PocketCampus.CloudPrint.ViewModels
{
    public sealed class MainViewModel : ViewModel<PrintRequest>
    {
        private readonly IPrintService _printService;
        private readonly IFileLoader _fileLoader;
        private readonly IFileUploader _fileUploader;
        private readonly PrintRequest _request;

        private PrintRequestStatus _status;
        private ColorConfig _colorConfig;
        private CopiesConfig _copiesConfig;
        private DoubleSidedConfig? _doubleSidedConfig;
        private MultiPageConfig _multiPageConfig;
        private PageOrientation _pageOrientation;
        private PageRange _pageRange;

        public PrintRequestStatus Status
        {
            get { return _status; }
            private set { SetProperty( ref _status, value ); }
        }

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

        public DoubleSidedConfig? DoubleSidedConfig
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

        public PageRange PageRange
        {
            get { return _pageRange; }
            set { SetProperty( ref _pageRange, value ); }
        }


        public AsyncCommand PrintCommand
        {
            get { return this.GetAsyncCommand( PrintAsync ); }
        }


        public MainViewModel( IPrintService printService, IFileLoader fileLoader, IFileUploader fileUploader,
                              PrintRequest request )
        {
            _printService = printService;
            _fileLoader = fileLoader;
            _fileUploader = fileUploader;
            _request = request;
        }

        private async Task PrintAsync()
        {
            Status = PrintRequestStatus.Printing;

            var serverRequest = new PrintDocumentRequest
            {
                ColorConfig = ColorConfig,
                CopiesConfig = CopiesConfig,
                DoubleSidedConfig = DoubleSidedConfig,
                MultiPageConfig = MultiPageConfig,
                Orientation = PageOrientation,
                Range = PageRange
            };

            if ( _request.DocumentId == null )
            {
                try
                {
                    var stream = await _fileLoader.GetFileAsync( _request.FileUri );
                    var documentId = await _fileUploader.UploadFileAsync( stream );
                    serverRequest.DocumentId = documentId;
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
                    // TODO don't forget to save state
                }
            }
            catch
            {
                Status = PrintRequestStatus.PrintingError;
                return;
            }

            Status = PrintRequestStatus.Success;
        }
    }
}