using System;
using System.IO;
using System.Threading.Tasks;

namespace PocketCampus.CloudPrint.Services
{
    public interface IFileLoader
    {
        Task<Stream> GetFileAsync( Uri uri );
    }
}