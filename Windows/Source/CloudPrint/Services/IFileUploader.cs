// Copyright (c) PocketCampus.Org 2014-15
// See LICENSE file for more details
// File author: Solal Pirelli

using System.IO;
using System.Threading.Tasks;

namespace PocketCampus.CloudPrint.Services
{
    public interface IFileUploader
    {
        Task<long> UploadFileAsync( string fileName, Stream fileContent );
    }
}