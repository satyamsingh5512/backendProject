import {v2 as cloudinary} from "cloudinary"
import fs from "fs"

cloudinary.config({
    cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
        api_key:process.env.CLOUDINARY_API_KEY,
        api_secret:process.env.CLOUDINARY_API_SECRET
});

const uploadOnCloudinary = async(localFilePath)=>{
    try{
        if(!LocalFilePAth) return null
const response = await cloudinary.uploader.upload(localFilePath,{
    resource_type:"auto",
})
//file uploaded
console.log("file uploaded to cloudinary",
    response.url);
    return response;   
    }catch(error){
        fs.unlinkSync(localFilePath) //Remove the loaclly save dtempoaray file as the upload operation gets failed

        return null;
    }
}