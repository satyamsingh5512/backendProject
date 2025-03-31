import mongoose,{Schema} from "mongoose";
import jwt from "jsonwebtoken";
import bcrypt from "bcryptjs";

const UserSchema = new Schema({

username:{
    type:String,
    required:true, 
    unique:true,
    lowecase:true,
    trim:true,
    index:true
},
email:{
    type:String,
    required:true, 
    unique:true,
    lowecase:true,
    trim:true,
    
},

fullname:{
    type:String,
    required:true, 
    trim:true,
    index:true
},
avatar:{
    type:String, //Cloudinary url
    required:true, 
    unique:true,
    
},
coverimage:{
    type:String, //Cloudinary url
   
},
watchHistorty:{
    type:Schema.Types.ObjectId,
    ref:"video",
},

password:{
    type:String,
    required:[true,'Password is required'] 
    
},
refreshToken:{
    type:String
}
},
{
    timestamps:true
}


)

UserSchema.pre("save",async function(next){
    if(!this.isModified("password")){
        return next()
    }
    this.password = bcrypt.hash(this.password,10)
    next()
})

UserSchema.methods.isPasswordcorrect = async function(Password){
return await bcrypt.compare(password,this.password)
}

UserSchema.methods.generateAccessToken = function(){
    jwt.sign({
        _id:this.id,
        email:this.email,
        username:this.username,
        fullname:this.fullname,
    },
    process.env.ACCESS_TOKEN_SECRET,{
        expiresIn:process.env.ACCESS_TOKEN_EXPIRY
    }
)
}
UserSchema.methods.generateRefreshToken = function(){

    jwt.sign({
        _id:this.id,
        // email:this.email,
        // username:this.username,
        // fullname:this.fullname,
    },
    process.env.REFRESH_TOKEN_SECRET,{
        expiresIn:process.env.REFRESH_TOKEN_EXPIRY
    }
)
}
export const  User = mongoose.model("User", UserSchema)



